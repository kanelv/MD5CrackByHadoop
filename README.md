# MD5CrackByHadoop

Brute-force MD5 password cracker built on Hadoop MapReduce. Distributes the candidate space across multiple mappers for parallel cracking.

## Constraints

- Password charset: digits `0–9`
- Password length: 6 characters (search space: 1,000,000 candidates)
- MD5 implementation is custom (no `java.security.MessageDigest`)

## Architecture

| Component | Role |
|---|---|
| `CandidateRangeInputFormat` | Splits the numeric candidate space into N equal ranges |
| `CandidateRangeInputSplit` | Represents one range `[start, end)` |
| `CandidateRangeRecordReader` | Yields `(rangeBegin, rangeEnd)` key/value pairs |
| `PasswordCrackerMapper` | Iterates candidates in its range, hashes each, emits match |
| `PasswordCrackerReducer` | Writes `(encryptedPassword, plaintext)` to output |
| `TerminationChecker` | Polls an HDFS flag file; stops the mapper early once another task finds the password |
| `MD5Hash` | Pure-Java MD5 implementation |

## Build

```bash
mvn package
```

Produces `target/MD5CrackByHadoop-1.0-SNAPSHOT.jar`.

## Usage

```bash
hadoop jar target/MD5CrackByHadoop-1.0-SNAPSHOT.jar hadoop.CrackerDriver \
  <outputPath> <md5Hash> <numberOfSplits>
```

**Arguments:**

| # | Name | Example |
|---|---|---|
| 1 | HDFS output directory | `/output/crack1` |
| 2 | MD5 hash to crack | `E10ADC3949BA59ABBE56E057F20F883E` |
| 3 | Number of splits (= mappers) | `10` |

**Example** — crack MD5 of `123456`:

```bash
hadoop jar target/MD5CrackByHadoop-1.0-SNAPSHOT.jar hadoop.CrackerDriver \
  /output/result E10ADC3949BA59ABBE56E057F20F883E 10
```

Result is written to `/output/result/part-r-*` as `<md5>\t<plaintext>`.

## Dependencies

- Hadoop 3.3.6 (`hadoop-common`, `hadoop-mapreduce-client-core`)
- Java 8+
