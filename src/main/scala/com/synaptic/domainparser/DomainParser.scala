package com.synaptic.domainparser

class DomainParser {
  private val extract = new TLDExtract()

  def fetchDomain(string: String): String = {
    val result = extract.extract(string)
    if (result.registeredDomain.nonEmpty) {
      result.registeredDomain
    } else if (result.domain.nonEmpty) {
      // This handles IP addresses and single-label domains
      result.domain
    } else if (result.suffix.nonEmpty) {
      // This handles cases where only a TLD is provided
      result.suffix
    } else {
      throw new InvalidDomainException(string)
    }
  }
}
