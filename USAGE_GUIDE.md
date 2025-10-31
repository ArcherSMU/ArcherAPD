# Dictionary Attack Tool - Usage Guide

## Quick Start

### 1. Build the Project
```bash
cd code
mvn clean compile
```

### 2. Run the Main Application
```bash
# Using small dataset
mvn exec:java -Dexec.mainClass="org.example.DictionaryAttackApplication" -Dexec.args="../datasets/small/dictionary.txt ../datasets/small/in.txt ../datasets/small/out.txt"

# Using large dataset (10K users)
mvn exec:java -Dexec.mainClass="org.example.DictionaryAttackApplication" -Dexec.args="../datasets/large/dictionary.txt ../datasets/large/in.txt ../datasets/large/out.txt"
```

### 3. Run Concurrency Benchmark
```bash
mvn exec:java -Dexec.mainClass="org.example.ConcurrencyBenchmark" -Dexec.args="../datasets/large/dictionary.txt"
```

---

## Available Implementations

### Production (Current)
**Class**: `ConcurrentPasswordCracker` (uses Parallel Streams)
- **Performance**: 2-5ms for hashing phase
- **Usage**: Default in `DictionaryAttackApplication`
- **Best for**: Production use, general-purpose

### Benchmark Alternatives
**Location**: `org.example.service.concurrent.*`

1. **ParallelStreamsCracker** ⭐ RECOMMENDED
   - Parallel streams with IntStream
   - 2-5ms performance
   - Simplest code

2. **WorkStealingCracker**
   - Executors.newWorkStealingPool()
   - 5-8ms performance
   - Good load balancing

3. **ForkJoinCracker**
   - RecursiveAction with divide-and-conquer
   - 6-9ms performance
   - Work stealing built-in

4. **ThreadPoolCracker**
   - Fixed thread pool with 16 threads
   - 10-20ms performance
   - High overhead for this workload

5. **Sequential (Baseline)**
   - Single-threaded processing
   - 15-16ms performance
   - Reference for comparison

---

## Configuration Options

### Adjust Parallelism Level
```java
// Set before running application
System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
```

### Disable Concurrency
Edit `PasswordCracker.java`:
```java
// Change from:
ConcurrentPasswordCracker concurrent = new ConcurrentPasswordCracker(hashService, progressTracker);
hashToPassword = concurrent.buildHashLookupTableParallel(dictionary);

// To:
hashToPassword = buildHashLookupTableSequential(dictionary);
```

---

## Expected Output

### Normal Run
```
=== Dictionary Attack Tool ===
Loading dictionary...
Loaded 7516 unique passwords (deduplicated from 7976)
Building hash lookup table...
Processing 10000 users...
Found passwords for 8159 users

Results:
- Passwords found: 8159 / 10000 (81.59%)
- Hashes computed: 7516
- Duration: 173 ms

Output written to: out.txt
```

### Benchmark Run
```
=== Password Cracker Concurrency Benchmark ===
Available processors: 16
Loading dictionary: ../datasets/large/dictionary.txt
Dictionary loaded: 7516 passwords

Warming up...
Warmup complete.

Benchmark Results:
--------------------------------------------------
Sequential (Baseline):          16 ms
Parallel Streams:                3 ms  (5.3x faster)
Fixed Thread Pool (16):         11 ms  (1.5x faster)
Fork/Join Pool:                  7 ms  (2.3x faster)
Work Stealing Pool:              6 ms  (2.7x faster)
--------------------------------------------------
```

---

## Performance Tips

### 1. JVM Tuning
```bash
# Increase heap for large datasets
mvn exec:java -Dexec.mainClass="..." -Dexec.args="..." -Dexec.vmArgs="-Xmx4g"

# Enable GC logging
-Dexec.vmArgs="-Xlog:gc*:gc.log"

# Tune GC (G1 collector)
-Dexec.vmArgs="-XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 2. Profiling
```bash
# Java Flight Recorder
java -XX:StartFlightRecording=filename=recording.jfr,duration=60s -cp target/classes org.example.DictionaryAttackApplication ...

# Analyze with JMC
jmc -open recording.jfr
```

### 3. Benchmarking Best Practices
- Run multiple iterations (warmup JIT compiler)
- Use production-like data volumes
- Monitor CPU/memory during runs
- Test with different core counts

---

## Troubleshooting

### Issue: OutOfMemoryError
**Solution**: Increase heap size or process dictionaries in batches
```bash
-Dexec.vmArgs="-Xmx8g"
```

### Issue: Slow Performance
**Check**:
1. Are you using production build? `mvn clean compile`
2. Is JIT warmed up? Run multiple times
3. Is CPU throttled? Check system load
4. Correct parallelism level? Match core count

### Issue: Incorrect Results
**Verify**:
1. Input file format matches expected
2. Dictionary contains required passwords
3. SHA-256 hashes are lowercase hex
4. No duplicate users in input

---

## File Formats

### Dictionary File (dictionary.txt)
```
password
123456
qwerty
admin
...
```
- One password per line
- UTF-8 encoding
- Duplicates automatically removed

### Input File (in.txt)
```
username1:hash1
username2:hash2
username3:hash3
...
```
- Format: `username:sha256_hex_hash`
- SHA-256 hash in lowercase hex
- One user per line

### Output File (out.txt)
```
username1:cracked_password
username2:cracked_password
username3
...
```
- Found: `username:password`
- Not found: `username` (no colon)
- Preserves input order

---

## Development

### Run Tests
```bash
# Once unit tests are added
mvn test
```

### Build JAR
```bash
mvn clean package
java -jar target/dictionary-attack-1.0.jar ../datasets/large/dictionary.txt ../datasets/large/in.txt ../datasets/large/out.txt
```

### Add New Cracking Strategy
1. Create class implementing similar interface
2. Add to `ConcurrencyBenchmark` for comparison
3. Update `PasswordCracker` if production-ready

---

## API Reference

### DictionaryAttackApplication
**Main entry point**
```bash
java org.example.DictionaryAttackApplication <dictionary> <input> <output>
```

### ConcurrencyBenchmark
**Performance comparison tool**
```bash
java org.example.ConcurrencyBenchmark <dictionary>
```

### DebugHashCount
**Diagnostic utility**
```bash
java org.example.DebugHashCount <dictionary>
```

---

## Additional Resources

- **Optimization Summary**: See `OPTIMIZATION_SUMMARY.md`
- **Code Structure**: See `code/src/main/java/org/example/`
- **Benchmark Results**: Run `ConcurrencyBenchmark` for your system

---

*Last updated after Phase 3 completion (Concurrency Implementation)*
