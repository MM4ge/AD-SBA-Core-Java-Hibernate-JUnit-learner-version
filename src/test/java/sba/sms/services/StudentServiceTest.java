package sba.sms.services;

import jakarta.persistence.PersistenceException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class StudentServiceTest {

    static StudentService studentService;
    static List<Student> allStudents = new ArrayList<>(Arrays.asList(
            new Student("reema@gmail.com", "reema brown", "password"),
            new Student("annette@gmail.com", "annette allen", "password"),
            new Student("anthony@gmail.com", "anthony gallegos", "password"),
            new Student("ariadna@gmail.com", "ariadna ramirez", "password"),
            new Student("bolaji@gmail.com", "bolaji saibu", "password")
    ));
    static List<Course> allCourses = null;

    @BeforeAll
    static void beforeAll() {
        studentService = new StudentService();
        CommandLine.addData();
        allCourses = new CourseService().getAllCourses();
    }

    @Test
    void getAllStudents() {
        assertThat(studentService.getAllStudents()).hasSameElementsAs(allStudents);
    }

    @Test
    void getAllIndividualStudents()
    {
        for(Student s : allStudents)
        {
            assertThat(studentService.getStudentByEmail(s.getEmail())).isEqualTo(s);
        }
    }

    @Test
    void validateStudentEmpty()
    {
        assertThat(studentService.validateStudent("","")).isFalse();
    }

    @Test
    void validateStudentFalseEmail()
    {
        assertThat(studentService.validateStudent("fake@email.com", "password")).isFalse();
    }

    @Test
    void validateStudentFalsePassword()
    {
        assertThat(studentService.validateStudent(allStudents.get(0).getEmail(), "NotRealPassword")).isFalse();
    }

    @Test
    void validateStudentsTrue()
    {
        for(Student s : allStudents)
        {
            assertThat(studentService.validateStudent(s.getEmail(), s.getPassword())).isTrue();
        }
    }

    @Test
    void registerStudentToCourse()
    {
        Student s = allStudents.get(0);
        for(Course c : allCourses)
        {
            studentService.registerStudentToCourse(s.getEmail(), c.getId());
        }

        assertThat(studentService.getStudentByEmail(s.getEmail()).getCourses()).hasSameElementsAs(allCourses);
    }

    @Test
    void registerStudentToCourseInvalids()
    {
        Student s = allStudents.get(0);
        Course c = allCourses.get(0);
        // Both invalid
        assertThatThrownBy(() -> studentService.registerStudentToCourse("", -1))
                .isInstanceOf(HibernateException.class).hasMessageContaining("didn't match any known");
        // CourseID invalid
        assertThatThrownBy(() -> studentService.registerStudentToCourse(s.getEmail(), -1))
                .isInstanceOf(HibernateException.class).hasMessageContaining("didn't match any known");
        // Email invalid
        assertThatThrownBy(() -> studentService.registerStudentToCourse("", c.getId()))
                .isInstanceOf(HibernateException.class).hasMessageContaining("didn't match any known");
    }

    @Test
    void getStudentCourses()
    {
        Student s = allStudents.get(1);
        Course c = allCourses.get(0);
        assertThat(studentService.getStudentCourses(s.getEmail())).isEmpty();

        studentService.registerStudentToCourse(s.getEmail(), c.getId());
        assertThat(studentService.getStudentCourses(s.getEmail())).contains(c);
    }
}