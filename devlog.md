# 10-18-2026 4:05pm

Starting CS4348 Project 1. The goal is to build a three-process system: a driver, a logger, and an encryption program, all communicating via pipes.

I'll be using Java since it has good process support via the Process class and I'm already familiar with it. The architecture is straightforward:
- Driver launches Logger and Encryption as child processes
- Driver communicates with both via stdin/stdout streams (pipes)
- Logger writes timestamped messages to a log file
- Encryption handles Vigenère cipher with PASS, ENCRYPT, DECRYPT, QUIT commands

For this session I plan to implement the encryption program first since it's self-contained and testable independently. Then I'll implement the logger. The driver can come next session since it ties everything together.

The Vigenère cipher is case-insensitive and only works on letters. I'll assume uppercase input as specified.

# 10-18-2026 4:45pm

I've implemented the encryption program and logger. The encryption program handles PASS, ENCRYPT, DECRYPT, and QUIT commands. The Vigenère cipher implementation shifts letters based on the repeating key. The logger reads lines from stdin and writes them with timestamps in YYYY-MM-DD HH:MM format until it receives QUIT.

Testing the encryption locally with some sample input showed it works correctly. The logger also produces properly formatted output.

Next session I'll implement the driver program to launch both processes and wire up the pipes.

# 10-18-2026 5:10pm

Today I implemented the driver program. This was the most complex part since it needs to launch both child processes and manage the pipes connecting them.

Using Java's ProcessBuilder, I launch Logger with the log file name as an argument and Encryption with no arguments. I grab their stdin/stdout streams and wrap them in PrintWriters and Scanners for easy line-based communication.

The menu loop presents the five commands. For password, encrypt, and decrypt, the driver first asks whether to enter a new string or pick from history. Passwords are never stored in history per the spec, and all commands plus their results get logged.

I tested the full flow: set password HELLO, encrypt HELLO, and got OIWWC back which matches the spec example exactly. The log file correctly shows the START entry and each command.

I noticed one issue to fix - the history selection needs testing with the cancel option, and I should verify error handling for non-letter input.
