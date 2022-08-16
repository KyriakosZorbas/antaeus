### Solution

In this solution, a custom scheduler is used that runs the Billing Process every 1st of the month.

The *BillingService* is initialized by the main function of the module *pleo-antaeus-app* upon start. Once the service starts, it checks if it is the 1st of the month. If it is not then the service remains idle until the 1st day of the next month.

When it is the 1st of the month, the service processes only the invoices that are stored in the database and have pending status. If the process of the invoice is succesful then it updates its status in the database from *PENDING* to *PAID*. Furthermore, *BillingService* executes 3 times before goes idle in order to avoid loss due to network exceptions.

I implemented the following features and test them with Unit tests.

* Fetch invoices by status
* Charge Pending invoices
* Update Database with paid invoices
* Endpoint to charge pending invoices
* Execute the Billing process each month
* Add logger

### Documentation
The API Testing documentation can be found [here](https://documenter.getpostman.com/view/9134047/VUjTihej). It is documented with the help of the tool named [Postman](https://www.postman.com/) because it provides dynamic examples and machine-readable instructions.

### Future work
As a future work the following enchanments could be done

* Add a robust scheduler like [Quartz](http://www.quartz-scheduler.org/)
* Add batch processing to handle large volumes of data
* Add Pub-sub system like [Kafka](https://kafka.apache.org/)
* Add loadbalancer (kubernetes) to open or close instances to handle the invoices. This could scale vertical or horizontal
* Add [Swagger](https://swagger.io/) for the API documentation
* Add notification system
* Add Integration tests

# Authentication/Authorisation

If we want to add authentication we can use a third-party tool named [Auth0](https://auth0.com/). Auth0 is an easy to implement, adaptable authentication and authorization platform. Auth0 supports multiple protocols, one of the most suitable for this app's case would be **OAuth 2.0** which allows a user to grant limited access to their resources on one site to another site, without having to expose their credentials.

## Time spent

| Task | Time spend (h) |
| ------ | ------ |
| Features | 5 |
| Testing | 7 |
| Documents | 1 |

## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will schedule payment of those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

## Instructions

Fork this repo with your solution. Ideally, we'd like to see your progression through commits, and don't forget to update the README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

## Developing

Requirements:
- \>= Java 11 environment

Open the project using your favorite text editor. If you are using IntelliJ, you can open the `build.gradle.kts` file and it is gonna setup the project in the IDE for you.

### Building

```
./gradlew build
```

### Running

There are 2 options for running Anteus. You either need libsqlite3 or docker. Docker is easier but requires some docker knowledge. We do recommend docker though.

*Running Natively*

Native java with sqlite (requires libsqlite3):

If you use homebrew on MacOS `brew install sqlite`.

```
./gradlew run
```

*Running through docker*

Install docker for your platform

```
docker build -t antaeus
docker run antaeus
```

### App Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── buildSrc
|  | gradle build scripts and project wide dependency declarations
|  └ src/main/kotlin/utils.kt 
|      Dependencies
|
├── pleo-antaeus-app
|       main() & initialization
|
├── pleo-antaeus-core
|       This is probably where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|       Module interfacing with the database. Contains the database 
|       models, mappings and access layer.
|
├── pleo-antaeus-models
|       Definition of the Internal and API models used throughout the
|       application.
|
└── pleo-antaeus-rest
        Entry point for HTTP REST API. This is where the routes are defined.
```

### Main Libraries and dependencies
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library
* [Sqlite3](https://sqlite.org/index.html) - Database storage engine

Happy hacking 😁!
