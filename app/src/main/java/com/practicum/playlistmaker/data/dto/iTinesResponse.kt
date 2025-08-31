package com.practicum.playlistmaker.data.dto

class iTinesResponse (val resultCount: Int,
                      val results: List<TrackDto>) : Response()