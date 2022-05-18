package sba.sms.models;

import jakarta.persistence.*;
import lombok.*;

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

@Entity
@Table(name = "course")
public class Course {

    // id	int	Course unique identifier	Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    // name	String	Course name	50 character limit, not null
    @NonNull
    @Column(length = 50, nullable = false)
    String name;

    // instructor	String	Instructor name	50 character limit not null
    @NonNull
    @Column(length = 50, nullable = false)
    String instructor;

    // students	List<Student>	Course learners list	fetch type eager, cascade all except remove, mappedBy courses
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            mappedBy = "courses")
    List<Student> students;

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id && name.equals(course.name) && instructor.equals(course.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, instructor);
    }

   public void addStudent(Student s) {
       if (s != null && !students.contains(s)) {
           students.add(s);
           s.getCourses().add(this);
       }
   }

    public void removeStudent(Student s)
    {
        if(s != null) {
            students.remove(s);
            s.getCourses().remove(this);
        }
    }
}