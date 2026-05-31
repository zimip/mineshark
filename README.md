# Mineshark

Mineshark is a lightweight network analysis and utility mod built for the Fabric loader. It operates at a low level within the Netty network pipeline to intercept, log, and manipulate inbound and outbound network packets between the Minecraft client and server.

## Features

* Low-Level Packet Sniffing: Intercepts network traffic directly from the Netty channel pipeline before Minecraft handles or dispatches it, ensuring accurate packet logs regardless of standard mapping updates.
* Lag Simulation (Packet Delay): Introduces a custom delay (measured in milliseconds) to incoming and outgoing play-phase packets to safely simulate artificial latency.
* Connection Safety Bypass: Automatically detects and prioritizes lifecycle-critical processes (Handshake, Login, Encryption Tasks, Configuration, and KeepAlive events), processing them instantly to eliminate network disconnection errors or thread crashes.
* Anti-Flood Filter: Optional filtering rules to silence redundant high-frequency movement, positioning, and rotation updates, preventing console spam while preserving important interaction records.
* Atomic Packet Counters: Tracks the absolute index of every transmitted packet sequentially for precise data flow identification.

## Configuration

The default runtime behavior can be tweaked within the main mod entrypoint parameters:

* PACKET_DELAY_MS: Defines the artificial latency applied to valid play packets (default: 400). Set to 0 to disable delaying behavior entirely.
* FILTER_MOVEMENT: Toggles whether player movement updates are hidden from the logger output stream (default: true).
* BYPASS_KEEP_ALIVE: Forces critical network heartbeat checks to process with zero artificial latency, maintaining host connection stability.

## Requirements

* Minecraft 26.1.2 or compatible snapshots
* Fabric Loader (>= 0.19.2)
* Java Runtime Environment 25 or higher
