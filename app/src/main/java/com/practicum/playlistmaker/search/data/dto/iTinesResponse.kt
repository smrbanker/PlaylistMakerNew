package com.practicum.playlistmaker.search.data.dto

class iTinesResponse (val resultCount: Int,
                      val results: List<TrackDto>) : Response()