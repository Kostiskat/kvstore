# kvstore
A key-value datastore written in Java, including a simple client for experimenting and testing.

Very much in-progress; currently only supports Strings (and partial int support) with more types coming soon!

You can only experiment with 4 commands right now, via the CLI tool.
Future updates will include a custom SDK so anyone can use it.

- ``GET <key>`` - Get the value corresponding to ``<key>``
- ``SET <key> <value>`` - Set the value of ``<key>``
- ``DEL <key>`` - Delete this key-value entry from the database.
- ``INCR <key> [incr]`` - Increment an integer value by ``incr`` (default: 1)

Runs on port 6380.
