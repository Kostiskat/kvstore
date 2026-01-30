# kvstore
A key-value datastore written in Java, including a simple client for experimenting and testing.

Very much in-progress; currently only supports Strings (and partial int support) with more types coming soon!

You can only experiment with 3 commands right now, via the CLI tool.

- ``GET <key>`` - Get the value corresponding to ``<key>``
- ``SET <key> <value>`` - Set the value of ``<key>``
- ``INCR <key> [incr]`` - Increment an integer value by ``incr`` (default: 1)

Runs on port 6380.
