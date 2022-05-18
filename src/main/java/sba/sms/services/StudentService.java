package sba.sms.services;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;
import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.Collections;
import java.util.List;

@Log
public class StudentService implements StudentI {

    // getAllStudents	List<Student>	None	return all students from database, also handle commit,rollback, and exceptions
    /**
     * Retrieves all Students from the database and returns them in a List.
     * @return A List containing every Student in the database, or an empty one if there aren't any.
     */
    @Override
    public List<Student> getAllStudents() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query<Student> q = s.createQuery("from Student", Student.class);
        List<Student> ret = q.getResultList();

        s.close();
        return ret;
    }

    // createStudent	void	Student	persist student to database, also handle commit,rollback, and exceptions
    /**
     * Adds a Student to the database.
     * @param student The Student to add to the database. Assumed to be properly initialized (i.e. not null).
     */
    @Override
    public void createStudent(Student student) {
        Session s = null;
        Transaction t = null;
        try {
            s = HibernateUtil.getSessionFactory().openSession();
            t = s.beginTransaction();
            s.persist(student);
            t.commit();
        } catch (PersistenceException e) {
            if (t != null && !t.getRollbackOnly() && t.isActive())
                t.rollback();
            e.printStackTrace();
        }
        finally
        {
            if(s!= null)
                s.close();
        }
    }

    // getStudentByEmail	Student	String email	return student if exists, also handle commit,rollback, and exceptions

    /**
     * Retrieves a Student from the database based on their unique email primary key.
     * @param email The email of the Student to retrieve.
     * @return A matching Student if the email was found in the database, or null otherwise.
     * @throws HibernateException if email somehow matched multiple unique email IDs
     */
    @Override
    public Student getStudentByEmail(String email) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> q = s.createQuery("from Student where email = :email", Student.class);
            q.setParameter("email", email);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            log.info("Multiple students with the supplied email matched. Please report this error to IT.");
            e.printStackTrace();
            throw new HibernateException("Supplied email " + email + " matches multiple unique emails in the database. How?");
        }
    }

    // validateStudent	boolean	String email, String password	match email and password to database to gain access to courses, also handle commit,rollback, and exceptions
    /**
     *
     * @param email The Student's email, which is their primary key in the database
     * @param password The Student's password
     * @return True if the email and password are valid and both refer to the same Student, false otherwise
     */
    @Override
    public boolean validateStudent(String email, String password) {
        Student s = getStudentByEmail(email);
        return (s != null && s.getPassword().equals(password));
    }

    // registerStudentToCourse	void	String email, int courseId	register a course to a student (collection to prevent duplication), also handle commit,rollback, and exceptions
    /**
     * Registers a Student, identified by the email param, to a Course, identified by the courseId param.
     * @param email The email to retrieve the Student from the database with. Assumed to be valid.
     * @param courseId The id to retrieve the Course from the database with. Assumed to be valid.
     * @throws HibernateException If either the email doesn't match a Student or the courseId doesn't match a Course.
     */
    @Override
    public void registerStudentToCourse(String email, int courseId) {
        Session session = null;
        Transaction t = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            t = session.beginTransaction();

            Student student = getStudentByEmail(email);
            if(student == null)
                throw new HibernateException("Student email " + email + " didn't match any known Student.");
            Course c = new CourseService().getCourseById(courseId);
            if(c == null)
                throw new HibernateException("CourseID " + courseId + " didn't match any known Courses.");

            student.addCourse(c);
            session.merge(student);

            t.commit();
        }
        catch (PersistenceException e)
        {
            if(t != null && !t.getRollbackOnly() && t.isActive())
                t.rollback();
            // If it's a custom exception from up there
            if(e instanceof HibernateException)
                throw e;
            e.printStackTrace();
        }
        finally
        {
            if(session != null)
                session.close();
        }
    }

    // getStudentCourses	List<Course>	String email	get all the student courses list (use native query), also handle commit,rollback, and exceptions
    /**
     * Retrieves all Courses that a Student is enrolled in.
     * @param email The email of the Student to retrieve the Courses of. A
     * @return A List containing every Course the Student is enrolled in, or an empty list if they're enrolled in none
     *  or the email didn't map to a Student.
     */
    @Override
    public List<Course> getStudentCourses(String email) {
        Student s = getStudentByEmail(email);
        if(s == null)
            return Collections.emptyList();
        return s.getCourses();
    }
}
