package com.synaptic.domainparser

import scala.io.Source
import scala.util.matching.Regex
import java.net.{IDN, URL}

class TLDExtract(cacheFile: Option[String] = None) {
  private val SCHEME_RE = "^([abcdefghijklmnopqrstuvwxyz0123456789+.-]+:)?//".r
  private val IP_RE = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$".r
  private val PORT_RE = ":\\d+$".r
  private val TLDS_ALPHA_BY_DOMAIN = loadTlds()

  case class ExtractResult(subdomain: String, domain: String, suffix: String) {
    def registeredDomain: String = if (domain.nonEmpty && suffix.nonEmpty) s"$domain.$suffix" else ""
    override def toString: String = s"ExtractResult(subdomain='$subdomain', domain='$domain', suffix='$suffix')"
  }

  private def loadTlds(): Set[String] = {
    val source = cacheFile match {
      case Some(file) => Source.fromFile(file)
      case None => Source.fromResource("public_suffix_list.dat")
    }

    try {
      source.getLines()
        .filter(line => !line.startsWith("//") && line.trim.nonEmpty)
        .map(_.trim)
        .filter(!_.startsWith("!"))
        .toSet
    } finally {
      source.close()
    }
  }

  def extract(url: String): ExtractResult = {
    val (scheme, netloc) = splitSchemeNetloc(url)
    val netlocWithoutPort = PORT_RE.replaceFirstIn(netloc, "")
    extractFromNetloc(netlocWithoutPort)
  }

  private def splitSchemeNetloc(url: String): (String, String) = {
    val schemeMatch = SCHEME_RE.findFirstMatchIn(url)
    val (scheme, urlWithoutScheme) = schemeMatch match {
      case Some(m) => (m.group(1), url.substring(m.end))
      case None => ("", url)
    }
    val netloc = urlWithoutScheme.split("/").head
    (scheme, netloc)
  }

  private def extractFromNetloc(netloc: String): ExtractResult = {
    if (IP_RE.findFirstIn(netloc).isDefined) {
      return ExtractResult("", netloc, "")
    }

    val labels = IDN.toUnicode(netloc).split('.').reverse
    val (suffix, suffixLength) = getSuffix(labels)
    val (domain, subdomains) = getDomain(labels, suffixLength)

    ExtractResult(subdomains.reverse.mkString("."), domain, suffix.reverse.mkString("."))
  }

  private def getSuffix(labels: Array[String]): (List[String], Int) = {
    var suffixParts = List.empty[String]
    var i = 0
    while (i < labels.length) {
      val maybeSuffix = labels.take(i + 1).reverse.mkString(".")
      if (TLDS_ALPHA_BY_DOMAIN.contains(maybeSuffix)) {
        suffixParts = labels.take(i + 1).toList
        i += 1
      } else {
        return (suffixParts, i)
      }
    }
    (suffixParts, i)
  }

  private def getDomain(labels: Array[String], suffixLength: Int): (String, List[String]) = {
    if (suffixLength < labels.length) {
      (labels(suffixLength), labels.drop(suffixLength + 1).toList)
    } else {
      ("", List.empty)
    }
  }
}
