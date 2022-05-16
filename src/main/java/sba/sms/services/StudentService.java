package sba.sms.services;

import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;

import java.util.List;

public class StudentService implements StudentI {

    @Override
    public List<Student> getAllStudents() {
        return null;
    }

    @Override
    public void createStudent(Student student) {

    }

    @Override
    public Student getStudentByEmail(String email) {
        return null;
    }

    @Override
    public boolean validateStudent(String email, String password) {
        return false;
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {

    }

    @Override
    public List<Course> getStudentCourses(String email) {
        return null;
    }
}
