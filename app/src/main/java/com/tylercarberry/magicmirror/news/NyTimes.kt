package com.tylercarberry.magicmirror.news

import java.util.*

data class NyTimes(
    val status: String,
    val copyright: String,
    val section: String,
    val lastUpdated: Date,
    val numResults: Int,
    val results: List<Article>
)

data class Article(
    val section: String,
    val subsection: String,
    val title: String,
    val abstract: String,
    val url: String,
    val uri: String,
    val byline: String,
    val itemType: String,
    val updatedDate: Date,
    val createdDate: Date,
    val materialTypeFacet: String,
    val kicker: String,
    val desFacet: List<String>,
    val orgFacet: List<String>,
    val perFacet: List<String>,
    val geoFacet: List<String>,
    val multimedia: List<Multimedia>,
    val shortUrl: String
)

data class Multimedia(
    val url: String,
    val format: String,
    val height: Int,
    val width: Int,
    val type: String,
    val subtype: String,
    val caption: String,
    val copyright: String
)