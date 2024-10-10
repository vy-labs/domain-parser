# Domain Parser

This Scala project provides a domain parser that can extract the registered domain from a given URL or hostname.

## Features

- Extracts the registered domain from a URL or hostname

- Extracts the registered domain from a URL or hostname
- Handles subdomains, domains, and public suffixes
- Supports Internationalized Domain Names (IDN)
- Handles IP addresses
- Uses the Public Suffix List for accurate domain extraction

## Build

```
sbt assembly
```

This will create a JAR file in the `target/scala-2.12` directory.

## Usage in Scala

```scala
val parser = new DomainParser()
val domain = parser.fetchDomain("https://www.example.com")
println(domain) // Output: example.com
```

## Usage With Spark

To use the Domain Parser in your EMR or Spark application:

```python
spark = SparkSession.builder \
    .appName("DomainParserExample") \
    .config("spark.jars", "/path/to/your/domain-parser-assembly-0.1.jar") \
    .getOrCreate()

def parse_domain(url):
    domain_parser = spark._jvm.com.synaptic.domainparser.DomainParser()
    try:
        return domain_parser.fetchDomain(url)
    except:
        return None

parse_domain_udf = udf(parse_domain, StringType())

```

### Contribution

We welcome contributions to the project. Please feel free to submit a PR.
