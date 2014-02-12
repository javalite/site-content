Title: Explanation on Many to Many associations in ActiveJDBC

[Introduction](#Introduction)

[Example tables](#Example_tables)

[Models](#Models)

[Many to many operations](#Many_to_many_operations)

-   [Select all related objects](#Select_all_related_objects)
-   [Conditional selection of related objects](#Conditional_selection_of_related_objects)
-   [Checking for association](#Checking_for_association)
-   [Adding new entries](#Adding_new_entries)
-   [Removing Entries](#Removing_Entries)
-   [Deleting Entries](#Deleting_Entries)

[Overriding associations](#Overriding_associations)

[Real models for join tables](#Real_models_for_join_tables)

[Conclusion](#Conclusion)

Introduction
============

Often times the database-driven applications require many to many relationships. These are the kind where an entity can have many other entities and also belong to the same type of entities. Examples in real life are: doctor treats many patients, and a patient sees many doctors. Another examples is when a university course has many students and a student has registered for many courses. In order to replicate this type of a relationship, usually three tables are created, one for the first type of entity, the other for another type of entity and a middle table which binds entities from the first two tables.

Example tables
==============

Let's see an example based on doctors and patients.

Table DOCTORS:

~~~~ {.prettyprint}
+----+------------+-----------+-----------------+
| id | first_name | last_name | discipline      |
+----+------------+-----------+-----------------+
|  1 | John       | Doe       | otholaringology | 
|  2 | Hellen     | Hunt      | dentistry       | 
+----+------------+-----------+-----------------+
~~~~

Table PATIENTS:

~~~~ {.prettyprint}
+----+------------+-----------+
| id | first_name | last_name |
+----+------------+-----------+
|  1 | Jim        | Cary      | 
|  2 | John       | Carpenter | 
+----+------------+-----------+
~~~~

As you can see, there is nothing in these to tables that tell us that doctors and patinets are somehow related. The third table binds entities between the doctors and patients table:

Table DOCTORS\_PATIENTS:

~~~~ {.prettyprint}
+----+-----------+------------+
| id | doctor_id | patient_id |
+----+-----------+------------+
|  1 |         1 |          2 | 
|  2 |         1 |          1 | 
|  3 |         2 |          1 | 
+----+-----------+------------+
~~~~

Looking at this table, we can discern that a doctor with ID = 1 (John Doe) has two patients: Jim Cary and John Carpenter. However Jim Cary also sees doctor Hellent Hunt. Let's see what kind of a support ActiveJDBC provides when it comes to many to many relationship. We will use the same table we outlined above.

Models
======

Model for table DOCTORS:

~~~~ {.prettyprint}
public class Doctor extends Model {}
~~~~

Model for table PATIENT:

~~~~ {.prettyprint}
public class Patient extends Model {}
~~~~

Model for table DOCTORS\_PATIENTS:

~~~~ {.prettyprint}
public class DoctorsPatients extends Model {}
~~~~

In cases when you override conventions, creation of a model that represents a join table is optional

As usual, ActiveJDBC will use inflections to map these models to the tables. It also expects the DOCTORS\_PATIENTS table to have `doctor_id` and `patient_id` columns. If everything is named appropriately (there are ways to override these conventions, see below), then the many to many relationships are configured across Doctor and Patient models. All the usual CRUD operations are supported right out of the box:

Many to many operations
=======================

Select all related objects
--------------------------

The select API for many to many is identical that of one to many, The framework is smart enough figure this out:

~~~~ {.prettyprint}
//Let's lookup a doctor:
Doctor doctor = Doctor.findById(1);
//get all patients of this doctor
List<Patient> patients = doctor.getAll(Patient.class);
System.out.println("Doctor 1 has " + patients.size() + " patient(s)");//prints "Doctor 1 has 2 patient(s)"

//Lookup a second doctor:
doctor = Doctor.findById(2);
patients = doctor.getAll(Patient.class);
System.out.println("Doctor 2 has " + patients.size() + " patient(s)");//prints "Doctor 1 has 1 patient(s)"
~~~~

The framework will generate appropriate select statement and execute it across two tables. This allows focusing on objects and abstract away from tabular nature of data in the DB.

Conditional selection of related objects
----------------------------------------

ActiveJDBC provides a way to filter related objects. Let's say that there are tables PROGRAMMERS, PROJECTS and PROGRAMMERS\_PROJECTS. In this case, we will create a model `Assignments` that will represent the join table:

~~~~ {.prettyprint}
@Table("programmers_projects")
public class Assignments extends Model{}
~~~~

as well as other models:

~~~~ {.prettyprint}
public class Project extends Model{}
..
public class Programmer extends Model{}
~~~~

You can treat a Many-to-many relationship as two one-to-many relationships. In this case, you could say that a project has many assignments and a programmer has many assignments. Armed with this knowledge, we can write some code:

~~~~ {.prettyprint}
Programmer programmer = Programmer.createIt("first_name", "Jim", "last_name", "Garnoe");

Assignment assignment = Assignment.createIt("duration_weeks", 3);
//use one to many notation here:
programmer.add(assignment);
Project project1 = Project.createIt("project_name", "Prove theory of everything");
project1.add(assignment);

//use many to many notation:
Project project2 = Project.createIt("project_name", "Find meaning of life");
programmer.add(project2);
~~~~

at this point, the table PROGRAMMERS\_PROJECTS will have this content:

~~~~ {.prettyprint}
+----+----------------+------------+---------------+---------------------+---------------------+
| id | duration_weeks | project_id | programmer_id | created_at          | updated_at          |
+----+----------------+------------+---------------+---------------------+---------------------+
|  1 |              3 |          1 |             1 | 2010-10-04 14:08:04 | 2010-10-04 14:08:04 |
|  2 |           NULL |          2 |             1 | 2010-10-04 14:08:04 | 2010-10-04 14:08:04 |
+----+----------------+------------+---------------+---------------------+---------------------+
~~~~

Where the first assignment is set for 3 weeks, and a second has no duration\_weeks value.

Having this data, we can query many to many relationship using a select filter on a join table:

~~~~ {.prettyprint}
List<Project> projects = programmer.get(Project.class, "duration_weeks = ?", 3);
~~~~

The result will be only one record.

Checking for association
------------------------

This is pretty simple:

~~~~ {.prettyprint}
System.out.println(Patient.belongsTo(Doctor.class));//prints "true"
~~~~

BWT, the same API will also work for one to many relationship.

Adding new entries
------------------

In many to many associations, there are no parents or children, as both sides of the association are equal.

Adding new entries then is pretty easy:

~~~~ {.prettyprint}
Doctor doctor = Doctor.findById(1);
Patient patient = Patient.create("first_name", "Jim", "last_name", "Smith");
doctor.add(patient);
~~~~

Here you see an example of a shortcut for creation of models with the `create()` method. Again, the method for adding a new entity is the same for one to many relationship, and the framework figures out what to do based on what it knows about the models. Here, we are adding a newly created patient, which is does not exist in the database yet. In this case, the framework will create two new records in the DB: one for a new patient, and one in the DOCTORS\_PATIENTS table that binds a current doctor and a new patient.

In the case where a patient exists already, it will only add a join record in the DOCTORS\_PATIENTS table.

Removing Entries
----------------

Removing is also easy:

~~~~ {.prettyprint}
doctor.remove(patient);
~~~~

Here, only a join table record is being removed, the actual patient record stays unchanged. In this case, the API for removing a child is the same for one to many an many to many relationships, but semantics are different. In one to many association the child record will be removed from the DB.

Deleting Entries
----------------

Deleting entries is similar to deleting in One to Many associations:

~~~~ {.prettyprint}
doctor.deleteCascade();
~~~~

However, semantics are different. In many to many relationships, the `model.deleteCascade()` method will do more than just delete this record. It will also discover all associated join tables and will delete records from them that match this models' ID value, effectively dis-associating it from all many to many relationships. This process is efficient because it will issue one DELETE statement per relationship.

Overriding associations
=======================

In case the naming conventions cannot be used, you can override the convention to let the framework know which models are bound in many to many association:

~~~~ {.prettyprint}
@Many2Many(other = Course.class, join = "registrations", sourceFKName = "astudent_id", targetFKName = "acourse_id")
public class Student extends Model {}
~~~~

Here, "other" is a model that represents the other end of the relationship, "join" is a name of a join table (table in the middle), sourceFKName is a source foreign key name. A source is this model, in this case it is Student. This means that the framework will expect to find a column "astudent\_id" in the table "registrations" and will assume that it contains keys of records in the "student" table. targetFKName is similar to the sourceFKName, but stands for a column "acourse\_id" in the table "registrations" that contains keys to the records in the "courses" table.

The annotation @Many2Many is one-sided. This means that it provides enough information to the framework, and there is no need to add another one to the model Course (it will not break if you do though). ActiveJDBC follows DRY principle as much as possible.

NOTE: in case of using @Many2Many annotation, creation of a model that represent a join table is not optional.

Real models for join tables
===========================

Join tables are represented by real models, ActiveJDBC handles it transparently. To illustrate the above examples, for doctor - patient example, you might want to indicate where a specific patient is treated and you would then add a new column to the DOCTORS\_PATIENTS table called LOCATION. Then you would define a model like so:

~~~~ {.prettyprint}
@Table("DOCTORS_PATIENTS")
class DocPat extends Model{}
~~~~

In the case of the student/course, the join table already has a good name, so it is easy to define a new model:

~~~~ {.prettyprint}
class Registration extends Model{}
~~~~

The table REGISTRATIONS might have additional data, such as registration type, etc.

Conclusion
==========

This article showed how to use many to many relationships with ActiveJDBC. The APIs were designed with simplicity and ease of use in mind.

Back to [Features](Features) page.
