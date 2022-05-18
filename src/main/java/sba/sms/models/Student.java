package sba.sms.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

/*
no args constructor
all args constructor
required args constructor
setters and getter
toString (exclude collections to avoid infinite loops)
override equals and hashcode methods (don't use lombok here)
helper methods
 */
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Table(name = "student")
public class Student {

    // email	String	Student’s unique identifier	Primary key, 50 character limit, name email
    @NonNull
    @Id
    @Column(length = 50)
    String email;

    // name	String	Student’s name	50 character limit, not null, name name
    @NonNull
    @Column(length = 50, nullable = false)
    String name;

    // password	String	Student’s password	50 character limit not null, name password
    @NonNull
    @Column(length = 50, nullable = false)
    String password;
    // courses	List<Course>	Student courses list	Join table strategy name student_courses , name of student
    //  primary key column student_email and inverse primary key (courses) column courses_id , fetch type eager,
    //  cascade all except remove
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_email"),
            inverseJoinColumns = @JoinColumn(name = "courses_id"))
    List<Course> courses;

    @Override
    public String toString() {
        return "Student{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return email.equals(student.email) && name.equals(student.name) && password.equals(student.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, name, password);
    }

    public void addCourse(Course c)
    {
        if(c != null && !courses.contains(c)) {
            courses.add(c);
            c.getStudents().add(this);
        }
    }

    public void removeCourse(Course c)
    {
        if(c != null) {
            courses.remove(c);
            c.getStudents().remove(this);
        }
    }
}