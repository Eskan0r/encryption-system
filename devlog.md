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
