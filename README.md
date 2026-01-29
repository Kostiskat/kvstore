# kvstore
A key-value datastore written in Java, including a simple client for experimenting and testing.

Very much in-progress; currently only supports Strings with support for more types coming soon.
Multi-threaded performance also coming soon.
Data is currently lost from memory when the server shuts down. In the future it will be able to store and recover them incase of sudden failure.

You can only experiment with 2 commands right now

- ``GET <key>`` - Get the value corresponding to ``<key>``
- ``SET <key> <value>`` - Set the value of ``<key>``

Runs on port 6380.
