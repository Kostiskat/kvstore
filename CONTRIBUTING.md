# Contributing

First off, thanks for taking the time to contribute to kvstore! Your support is invaluable to us.

To get a better sense about how we work and how this project is structured, newcomers (or not) can refer
to this page for up-to-date information about contributing.

**For newcomers:** New contributors can look at GitHub issues labeled as "**good first issue**". Such issues are regarded as
critical to get a good understanding of how the database's fundamentals work.

----

### Get Started
To work on the core database or the Java SDK, you will need to set up your local development environment.

* **Prerequisities:** This project only works with <ins>Java 25+</ins>. In addition, Maven is required to be installed to be able to build the project.
* **Clone the repository:** Get your own offline copy of the repository.
  ```git clone https://github.com/Kostiskat/kvstore.git```
* **Build the project:** Run ```mvn clean install``` from the root directory to compile all the modules and run potential tests.
* **Run the server:** You can boot the server locally by running the `main` method of the server located in ```me.ccute.kvstore.server.Server```.

### Understanding the architecture

Before jumping into writing code, please take a moment to understand how our Maven modules work:

**Please note:** The module for the SDK is located in ``/sdk/java``. Other SDKs live or will live in the ``/sdk`` library
so be extra careful about where the SDK is built.

* ``server/``: The core database engine. This contains the Netty TCP pipeline and the AOF persistence logic.
* ``sdk/java``: The official Java SDK. This handles asynchronous pipelining and connection pooling.
* ``cli/``: CLI tools used to interact with the database from the terminal. A Benchmark script is added to test the read/write performance of the database.
* ``docs/``: Folder containing the documentation Markdown files as well as configuration files for doxygen.

### Submitting changes
We welcome all contributions, from tiny typo fixes to entire new SDK implementations in new languages.
To ensure a smooth review process, please follow these rules:

1. **Fork the repository** and create your branch from `main`.
2. **Write documentation** (optional) - in the `docs/` folder if you change an API or add a new configuration variable. It is totally fine if you don't write
   documentation along with your changes but note that the review process for your Pull Request will take longer as another contributor will have to do it.
3. **Ensure the build passes** when you make commits to a Pull Request before marking it as ready for review.

**Write well-documented code!** We use javadoc for documenting our public code, and you are expected to do so where appropriate.
Javadoc gets parsed on build time by doxygen to create the static documentation. Maintainers may request changes in
the documentation you write for it to be more concice and understandable.

When a Pull Request is marked as "Ready for Review", a member of the Quality Assurance team will take over and run various tests to make sure there are no
breaking changes and everything in your Pull Request works. If the QA passes, your PR will be ready to get merged.
Otherwise, you will be asked to make changes and fix any errors that occur.

### Where we need help
If you want to contribute but don't know where to start, here are our current priorities:

* **New SDKs:** We welcome official clients in Python, Node.js, Go, or Rust.
* **AOF Compaction:** Implementing a background thread to rewrite and compress the ``db.aof`` file.
* **More Commands & Types:** Expanding the dictionary of supported database commands (e.g., `EXISTS`) and types (e.g. arrays)

### Becoming a maintainer

We want to reward dedicated developers that actively contribute to our repository! Therefore, active contributors of this project
are invited to become official maintainers of kvstore. This means they are able to approve Pull Requests, request changes and housekeep
the GitHub repository.