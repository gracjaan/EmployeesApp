# EarnIt4
This platform facilitates
the administration of the work of students who are hired by companies for specific projects via the earnit platform. Students can submit their hours on a daily basis, view their past submissions and contracts. Companies can amongst other things view, accept or resuggest the hours submitted by the student. The earnit staff is provided with administrative tools like linking a student and company for a job, disabling users or resolving conflicts.

* [Introduction](#introduction)
   * [Installation](#installation)
   * [Usage](#usage)
      * [Student](#student)
         * [Dashboard](#dashboard)
            * [Week XX](#week-xx)
            * [Overview](#overview)
            * [Contracts](#contracts)
            * [Notifications](#notifications)
      * [Company](#company)
         * [Dashboard](#dashboard-1)
            * [Requests](#requests)
            * [Invoices](#invoices)
            * [Contracts](#contracts-1)
            * [Notifications](#notifications-1)
      * [Staff](#staff)
         * [Dashboard](#dashboard-2)
            * [Requests](#requests-1)
            * [Create Links](#create-links)
            * [Overview](#overview-1)
            * [Notifications](#notifications-2)
   * [Extra Features](#extra-features)
   * [Documentation](#documentation)
   * [Dependencies](#dependencies)
   * [Testing](#testing)
   * [Further improvements](#further-improvements)
   * [Contact Information](#contact-information)

---

## Introduction <a id="introduction"></a>
This application was made for the **Earn it start up company**. Earnit allows companies to hire student workers for short-term projects without having to provide a long-term commitment.
The students can then be hired in periods that they might have vacation or spare time.
This platform takes care of the time recording of the students that are hired by the companies. The companies are able to see all
the hours their students have worked and can consider to agree on the hours or to propose alternative hours to the student.

The Staff can manage the whole system and see all it's users. All conflicts can be resolved by the staff in this way.

---
## Installation <a id="installation"></a> 
For the installation of the application we do the following:

### Opening the project
- install intellij idea
- clone repository
- open project with intellij
- if necessary add a java jre, the project is java 17+
- make sure to reload maven

### Setting up tomcat
- go into edit configurations
- add a new tomcat local server
- go to the tomcat deployment tab
- add a new artifact and select the `:war` version
- change the application context to `/`

- go to the tomcat start/connection tab
- add the following environment variables, changes the values depending on your db:
   - DB_HOST: `localhost`
   - DB_PORT: `5432`
   - DB_NAME: `earnit`
   - DB_SCHEMA: `public`
   - DB_USER: `postgres`
   - DB_PASS: `postgres`
   - JWT_ISSUER: `earnit`
   - JWT_SECRET: `very-secure-secret`

### Setting up postgresql
- install [postgresql](https://www.postgresql.org/download/) locally
- create a new database
- create a schema or use the default public schema
- run the sql for the earnit db. This can be found under `src/main/assets/`. Make sure to use the latest version.
- to add an administrator adapt the following sql:
   a password can be generated [here](https://bcrypt-generator.com/) and copy the hash into password field.
   ```sql
   insert into "user" (email, first_name, last_name_prefix, last_name, password, type) VALUES ('staff@example.com', 'Firstname', NULL, 'Lastname', '<password hash>', 'ADMINISTRATOR');
   ```
- make sure to update the environment variables of tomcat to your postgresql db

---

## Packaging <a id=""></a>
The project is packaged as follows:
- `docs` contains all assignments for the university
- `tailwind.config.js` only exists for autocompletion
- `pom.xml` contains all the project and library information for maven
- `earnit.iml` contains the project information for intellij
- `src` contains the source code for the project
  - `assets` contains assets for the tests
  - `java` contains the java code for the project
    - `dao` contains all dao's to interact with the database
    - `dto` contains all dto's to interact between js and java
    - `exceptions` contains all custom exceptions
    - `filters` contains the redirect filter for the servlets
    - `helpers` contains helpers methods for the database and servlets
    - `models` contains all database models for jaxb
    - `resources` contains all servlet resources
    - `test` contains all tests
    - `Auth.java` contains authentication helper methods
    - `Contains.java` contains constants to easily change basic configurations
    - `TestDB.java` contains methods to set up the dynamic database for the tests
  - `webapp` contains html, js, css and other files for the web server

---


## Usage <a id="usage"></a>
The application has three different dashboards for different roles within the system. The Three roles being: Student, Company, Staff
### Student
In order to be on the application as a student, we first have to make an account for a student. This can be done by:
1. Go to the /signup page
2. On this page you are prompted with whether you want a student or company account. You need to click on the Student and continue.
3. You will then be prompted with the necessary input fields to fill in. If these are not filled in, an error will show up.
   * KVK number: an 8-digit number.
   * BTW number: for example NL123456789B01

>If all the inputs have been filled in, you are allowed to log in to the system. **The dashboard** page will appear when logged in. This page contains
the following actions to proceed with:
#### Dashboard <a id="dashboard"></a>
###### Week XX <a id="week-xx"></a>
Students can fill in their hours in the week tab. When landing on the page, the displayed week is always the current week. Clicking on the week will 
enable a dropdown menu for the user, displaying other weeks to fill in hours for. The user can then start to fill in their hours. This is done by choosing 
the day on which the hours were worked, how many hours were worked, the position the student was in during execution of the task (A student can work in multiple positions, at multiple companies), and a description of what the task entailed
The hours are submitted by pressing on the submit button. The hours will then be displayed on the page and can be sorted on hours and date.

When the week is over, the student can confirm the submission of the hours by clicking on the checkmark next to the week (after that a red cross will appear). The student is allowed to roll back the confirmation by clicking on the red cross only 
if the current week is not over yet. When the week is over, the student is not allowed to change their hours.

>The hours of a student might be flagged by the company. The student will get a notification of this, and it will show next to the hours of the flagged week
The Student then also is allowed to accept the newly suggested hours by the company or can decide not to agree.
The student accepts the suggested hours of the company by pressing on the checkmark, and rejects them by clicking on the lamp.

###### Overview <a id="overview"></a>
The overview shows the statistics of the students executed work. This is displayed in a graph which has the amount of hours on the y-axis
and the week on the x-axis. A student may have different contracts with different companies. This is represented by the different colors in the graph

Below the graph we have an overview of all invoices for the student. This can be sorted on the week, date and hours. 
A student can then download an invoice to see the details of that particular week. There also is the option for the student to
download all the invoices at once. This will create a zip-file containing all the invoices in PDF format.

###### Contracts <a id="contracts"></a>
The contract page shows a carousel which contains all the contracts that the user is currently engaged in.

###### Notifications <a id="notifications"></a>
The notifications that are displayed on the dashboard will appear when one of the following scenarios occur:
* Forgot to confirm hours
* A week is approved
* A rejected week has come in with a new suggestion of hours by the company
* A link has been created between the company and a student
---

### Company <a id="company"></a>
In order to be on the application as a Company, we first have to make an account for a Company. This can be done by:
1. Go to the /signup page
2. On this page you are prompted with whether you want a student or company account. You need to click on the Company and continue.
3. You will then be prompted with the necessary input fields to fill in. If these are not filled in, an error will show up.
   * KVK number: an 8-digit number.
   * BTW number: for example NL123456789B01

>If all the inputs have been filled in, you are allowed to log in to the system. A dropdown menu will appear when the company user is logged in. A company can be chosen here, since a company user might work for different companies. When a company is chosen, **the dashboard** page will show. This page contains
the following actions to proceed with:
#### Dashboard <a id="dashboard-1"></a>
###### Requests <a id="requests"></a>
The Requests page contains all the requests that the Student posted. These requests contain their worked week with the according hours.
The Requests can then be clicked on. This will show the week of the Student who submitted it. The name of the Student from whom the request is, will appear on the top. Clicking on this name will lead you to the info page of the Student. 

The company can decide to either accept 
the hours of the students(click on the checkmark), or to reject the hours filled in by the student. Confirming or rejecting can also be undone by clicking on the newly appearing button when rejecting or accepting.
If the hours are rejected, the company needs to make a new suggestion by clicking on the pencil sign next to the hours, and filling in the new hours.
The rejected hours are sent back to the student again (If the Student decides to reject the suggestion of hours by the company, it goes to the staff)

###### Invoices <a id="invoices"></a>
The Invoices tab shows all the invoices for all the employees of the company. This can be sorted by week, contract, hours, and user.
The company can decide to download all invoices by clicking on the download icon on the top, or to download only particular invoices.
Downloading particular invoice can be done by clicking on the download icon in an invoice row

###### Contracts <a id="contracts-1"></a>
The contract page shows all the contracts that the company has. Within the contract, the students who are linked to the contract are displayed.
Students which are displayed within the contract, can be clicked on. This will lead you to a page that shows information about the Student.

###### Notifications <a id="notifications-1"></a>
The notifications that are displayed onn the dashboard will appear when one of the following scenarios occur:
* Student accepted the suggestion new of hours
* Student rejects the suggestion of new hours
* A link has been created between the company and a student
 ---
### Staff <a id="staff"></a>
In order to be on the application as a Staff, we first have to make an account for an Administrator. An administrator cannot be created through the
application to enhance security. This is why we insert the Administrator into the database by hand

>If the administrator is inserted, the credentials can just be filled in, in the login. To insert an administrator look at the [installation](#installation) guide
#### Dashboard <a id="dashboard-2"></a>
###### Requests <a id="requests-1"></a>
The requests page contains all the flagged requests. If a company and a student couldn't agree on the hours that were filled in
The request will be handled by the staff. When on the requests page, you can click on a single request. This displays the week
with all the hours that the student suggested and the hours that the company suggested. The Student and the company both have added
comments to their suggestion so that the company gets context on why the request was flagged. 

The Staff then gets to decide to accept the hours of the Company or the Student.
###### Create Links <a id="create-links"></a>
The links page is used to creat links between the Students and the Companies. First the Student can be chosen by clicking on the dropdown menu: "choose user". 
Then the company is chosen by clicking on the dropdown menu: "choose company". Contracts that belong to the selected company will appear.
Select the contract that you want to associate to the user. The last thing that needs to be done is to fill in the associated pay with the contract. The link can then be created by clicking on: "create link"

###### Overview <a id="overview-1"></a>
The overview page will show all the Students and Companies who are on the platform. Students and Companies can be disabled by clicking on the red cross or the green checkmark.
When the there is a green checkmark, it means that the entry is disabled, and when there is a cross, the user is enabled. Clicking on the Student or Company
prompts you with another page which shows you all the information about the user.

###### Notifications <a id="notifications-2"></a>
The notifications that are displayed onn the dashboard will appear when one of the following scenarios occur:
* There is a conflict between a Student and a Company
---

## Extra Features <a id="extra-features"></a>
**Extra features are added to the application in order to make the user experience more enjoyable**:
1. **Search bars**, for the pages that display a list of either users, contracts or weeks, searchbars are added so that the right entry can be found fast
2. **filters**, The same accounts for filters. The filters can either go in descending or ascending order, and they can be disabled
3. **Graphs**, The user is able to see their progress in the graph which allows them to improve on their performance
4. **Notifications**, whenever there is something important that has changed in the system, you get a notification that states wat is changed and where to go review the change
5. **popups**, Whenever an action is taken that has a big impact on the system, a pop-up will be shown that asks you to confirm your action.
Alerts are also replaced with nice formatted pop-ups
6. **Settings**, the user can change their personal information by clicking on the gear icon in the top right corner. This allows the user to edit their name, email, kvk-number and btw-number

---

## Documentation <a id="documentation"></a>
**The documentation for the code is the following:**
* Java: all the documentation for the Java is in the JavaDoc
* JavaScript: all the documentation is done by comments in the JavaScript
* SQL: The SQL schema is explained by the database schema provided with the folder

---

## Dependencies <a id="dependencies"></a>
**For the dependencies we use Maven to update and install all the dependencies immediately.
The dependencies that we use are:**
* jakarta.servlet-api, version: 6.0.0, [https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api/6.0.0]()
* jakarta.ws.rs-api, version: 3.1.0, [https://mvnrepository.com/artifact/jakarta.ws.rs/jakarta.ws.rs-api/3.1.0]()
* jakarta.xml.bind-api, version: 4.0.0, [https://mvnrepository.com/artifact/jakarta.xml.bind/jakarta.xml.bind-api/4.0.0]()
* jackson-jakarta-rs-json-provider, version: 2.15.0, [https://mvnrepository.com/artifact/com.fasterxml.jackson.jakarta.rs/jackson-jakarta-rs-json-provider/2.15.0]()

* junit-jupiter-api, version: 5.9.2, [https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.9.2]()
* junit-jupiter-migrationsupport, 5.9.2, [https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-migration-support ](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-migration-support)

* jersey-container-servlet, version 3.1.1, [https://mvnrepository.com/artifact/org.glassfish.jersey.containers/jersey-container-servlet/3.1.1]()
* jersey-container-servlet-core, version 3.1.1 [https://mvnrepository.com/artifact/org.glassfish.jersey.containers/jersey-container-servlet-core/3.1.1]()
* jersey-media-jaxb, version: 3.1.1, [https://mvnrepository.com/artifact/org.glassfish.jersey.media/jersey-media-jaxb]()
* jersey-client, version: 3.1.1, [https://mvnrepository.com/artifact/org.glassfish.jersey.core/jersey-client/3.1.1]()
* jersey-hk2, version3.1.1

* jaxb-runtime, version: 4.0.2, [https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-runtime/4.0.2]()

* postgresql, version: 42.6.0, [https://mvnrepository.com/artifact/org.postgresql/postgresql/42.6.0]()
* java-jwt, version: 4.4.0, [https://mvnrepository.com/artifact/com.auth0/java-jwt/4.4.0]()
* bcrypt, version: 0.10.2, [https://mvnrepository.com/artifact/at.favre.lib/bcrypt/0.10.2]()
* openhtmltopdf-core, version: variable, [https://mvnrepository.com/artifact/com.openhtmltopdf/openhtmltopdf-core]()
* openhtmltopdf-pdfbox, version: variable, [https://mvnrepository.com/artifact/com.openhtmltopdf/openhtmltopdf-pdfbox]()
* openhtmltopdf-svg-support, version: variable, [https://mvnrepository.com/artifact/com.openhtmltopdf/openhtmltopdf-svg-support]()

* jsoup, version: 1.16.1, [https://mvnrepository.com/artifact/org.jsoup/jsoup/1.16.1]()

* embedded-postgres, 2.0.4, [https://mvnrepository.com/artifact/io.zonky.test/embedded-postgres]()

---

## Testing <a id="testing"></a>
All tests are unit tests. These can be run inside an idea or command line. The tests use a dynamic database. Because of this dynamic database some tests may take a bit of time. 
Sadly, this dynamic database doesn't seem to work on any apple operating system.
If you encounter the following error `Could not load db schema file`. Mark the `src/main/assets` as sources.
---

## Further improvements <a id="further-improvements"></a>  
### Forgot password (restore question)
In the current system, when a user has forgotten their password, they are not able to retrieve their account without the help of staff.
A new version should contain the functionality for the user to be able to request a new password via email so that they can log in again.

### Start and end date to a user contract
User contracts are now terminated by staff. In the future this should be handled by the company.
The company can then create link between a user and a contract, with a start and end date associated to it.

### Emails
The current system is not integrated with an email server, therefore emails cannot be sent to the user. This improvement would also enable the implementatoin of the 'forgot password' feature.
Emails would allow us to not only display the notifications in the application,
but also email them to the user.

---


## Contact Information <a id="contact-information"></a>
**This project was produced by:**  
Thomas Brants s2997894 t.g.a.brants@student.utwente.nl,  
Razvan Stefan, s2957868, r.stefan@student.utwente.nl  
Gracjan Chmielnicki s3077489 g.s.chmielnicki@student.utwente.nl  
Tom Hansult, s2993074, t.hansult@student.utwente.nl  
Pepijn Meijer, s2957566, p.j.meijer@student.utwente.nl

