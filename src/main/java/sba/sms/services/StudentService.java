package sba.sms.services;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.List;

public class StudentService implements StudentI {

    @Override
    public List<Student> getAllStudents() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query<Student> q = s.createQuery("from student", Student.class);
        List<Student> ret = q.getResultList();

        s.close();
        return ret;
    }

    @Override
    public void createStudent(Student student) {
        Transaction t = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            t = s.beginTransaction();
            s.persist(student);
            t.commit();
        } catch (HibernateException e) {
            if (t != null)
                t.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Student getStudentByEmail(String email) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query<Student> q = s.createQuery("from student where email = :email", Student.class);
        q.setParameter(email, "email");
        Student ret = q.getSingleResult();

        s.close();
        return ret;
    }

    @Override
    public boolean validateStudent(String email, String password) {
        Student s = getStudentByEmail(email);
        return (s != null && s.getPassword().equals(password));
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {
        Student s = getStudentByEmail(email);
        CourseService cs = new CourseService();
        Course c = cs.getCourseById(courseId);

        s.addCourse(c);
    }

    @Override
    public List<Course> getStudentCourses(String email) {
        Student s = getStudentByEmail(email);
        return s.getCourses();
    }
}
