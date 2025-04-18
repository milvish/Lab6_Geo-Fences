/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.treasureHunt

import android.content.Context
import android.util.Log
//import android.util.JsonReader
import com.example.android.treasureHunt.GeofencingConstants.LANDMARK_DATA
import com.example.android.treasureHunt.GeofencingConstants.NUM_LANDMARKS
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import com.google.gson.stream.JsonReader


/**
 * Returns the error string for a geofencing error code.
 */
fun errorMessage(context: Context, errorCode: Int): String {
    val resources = context.resources

    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> resources.getString(
            R.string.geofence_not_available
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> resources.getString(
            R.string.geofence_too_many_geofences
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> resources.getString(
            R.string.geofence_too_many_pending_intents
        )
        else -> resources.getString(R.string.unknown_geofence_error)
    }
}

fun getCoordinates(context: Context) {

    val jsonInputStream = context.assets.open("geofencing_lab_landmarks.json")
    val jsonReader = JsonReader(InputStreamReader(jsonInputStream))
    val gson = Gson()
    val jsonFile: JsonObject = gson.fromJson(jsonReader, JsonObject::class.java)

    val landmarks = jsonFile.getAsJsonArray("geofencing")
    for (i in 0 until landmarks.size()) {
        val landmarkObject = landmarks[i].asJsonObject
        val locationObject = landmarkObject.getAsJsonObject("location")
        val latitude = locationObject["latitude"].asDouble
        val longitude = locationObject["longitude"].asDouble

        LANDMARK_DATA.add(
            LandmarkData(
                id = landmarkObject["id"].asString,
                hint = landmarkObject["hint"].asString,
                name = landmarkObject["name"].asString,
                latLong = LatLng(latitude, longitude)
            )
        )
    }
    Log.d("Geofence", "Landmarks $LANDMARK_DATA")
    NUM_LANDMARKS = LANDMARK_DATA.size
}

/**
 * Stores latitude and longitude information along with a hint to help user find the location.
 */
//data class LandmarkDataObject(val id: String, val hint: Int, val name: Int, val latLong: LatLng)

data class LandmarkData(val id: String, val hint: String, val name: String, val latLong: LatLng)

internal object GeofencingConstants {

    /**
     * Used to set an expiration time for a geofence. After this amount of time, Location services
     * stops tracking the geofence. For this sample, geofences expire after one hour.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)

    val LANDMARK_DATA = arrayListOf<LandmarkData>()



//    val temp_landmarks = arrayListOf(
//        LandmarkDataObject(
//            "golden_gate_bridge",
//            R.string.golden_gate_bridge_hint,
//            R.string.golden_gate_bridge_location,
//            LatLng(37.819927, -122.478256)),
//
//        LandmarkDataObject(
//            "ferry_building",
//            R.string.ferry_building_hint,
//            R.string.ferry_building_location,
//            LatLng(37.795490, -122.394276)),
//
//        LandmarkDataObject(
//            "pier_39",
//            R.string.pier_39_hint,
//            R.string.pier_39_location,
//            LatLng(37.808674, -122.409821)),
//
//        LandmarkDataObject(
//           "union_square",
//            R.string.union_square_hint,
//            R.string.union_square_location,
//            LatLng(37.788151, -122.407570))
//    )

    var NUM_LANDMARKS = LANDMARK_DATA.size
    const val GEOFENCE_RADIUS_IN_METERS = 20f
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}
