# Budget Pace Engine

## Problem

Ad platforms need to decide in real-time whether an ad should be served based on campaign budget limits. With thousands of concurrent ad requests, the system must:
- Check budget availability instantly (<1ms)
- Prevent budget overruns when multiple ads hit simultaneously
- Track daily spending limits accurately
- Handle duplicate requests gracefully

## Solution

A high-performance budget pacing engine that uses Redis for atomic budget operations and PostgreSQL for persistence.

## How It Works

**1. Initialization**
- Campaigns and ads are stored in PostgreSQL
- On startup, all data loads into Redis cache
- Each campaign gets: `dailySpendLimit`, `remainingBudget`, `lastUpdated`

**2. Eligibility Check Flow**
```
Request (adId, cost, requestId) 
  → Check idempotency (requestId)
  → Lookup ad's campaign in Redis
  → Check if 24hrs passed → reset budget if needed
  → Atomically deduct cost from remainingBudget
  → If insufficient → rollback and reject
  → Return {eligible, costRemaining, expiresAt}
```

**3. Background Jobs**
- **Midnight**: Reset all campaign budgets to daily limit
- **1 AM**: Remove expired campaigns from cache
- **Every 5 min**: Sync campaign limit changes from DB to Redis

## Example

Campaign has $100 daily limit:
- Ad1 costs $10 → Approved, $90 remaining
- Ad2 costs $30 → Approved, $60 remaining  
- Ad3 costs $80 → Rejected, $60 remaining (insufficient)

## Key Features

- **Atomic Operations**: Synchronized blocks prevent race conditions
- **Idempotency**: Same requestId returns cached result
- **No DB Writes**: Eligibility checks only read/write Redis
- **Auto Reset**: Budget resets after 24 hours from last update
- **Low Latency**: Sub-millisecond response via in-memory operations

## Quick Start

```bash
createdb budget_pace
redis-server
mvn spring-boot:run
```

## API

**POST** `/api/budget/check-eligibility`
```json
{"adId": "ad1", "cost": 10, "requestId": "unique-id"}
```

**Response:**
```json
{"eligible": true, "costRemaining": 90, "expiresAt": "2025-01-18"}
```
