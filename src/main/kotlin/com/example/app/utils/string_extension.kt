package com.example.app.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.cfg.MutableConfigOverride
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule



fun Any.objectToJson(): String {
    return ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(this)
}


fun Any.toNodeJson(): JsonNode {
    return ObjectMapper().readTree(this.objectToJson())
}


fun <T> JsonNode.toListObject(clazz: Class<T>): List<T> {
//    val mapper = JsonMapper.builder()
//        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        .changeDefaultPropertyInclusion { i: JsonInclude.Value? ->
//            i!!.withValueInclusion(JsonInclude.Include.NON_NULL) }
//
//        .withCoercionConfig(String::class.java) {
//                cfg: MutableCoercionConfig ->
//            cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull)
//                .setAcceptBlankAsEmpty(true)
//
//        }
//        .withConfigOverride(String::class.java) {
//                cfg: MutableConfigOverride -> cfg.setNullHandling(JsonSetter.Value.forValueNulls(Nulls.SKIP))
//        }
//        .build()

    val result = arrayListOf<T>()
    try {
        this.forEach {
            result.add(it.toObject(clazz)?:clazz.getDeclaredConstructor(Any::class.java)
            .newInstance())
        }
    }
    catch (_: Exception) {
        emptyList<T>()
    }
    return result
}


fun <T> JsonNode.toObject(clazz: Class<T>): T? {
    val mapper = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .changeDefaultPropertyInclusion { i: JsonInclude.Value? ->
            i!!.withValueInclusion(JsonInclude.Include.NON_NULL) }
        .withConfigOverride(String::class.java) {
                cfg: MutableConfigOverride -> cfg.setNullHandling(JsonSetter.Value.forValueNulls(Nulls.SKIP))
        }.build()
    return mapper.treeToValue(this, clazz)

}


private val objectMapper = JsonMapper.builder()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .changeDefaultPropertyInclusion { i: JsonInclude.Value? ->
        i!!.withValueInclusion(JsonInclude.Include.NON_NULL) }
    .withConfigOverride(String::class.java) {
        cfg: MutableConfigOverride -> cfg.setNullHandling(JsonSetter.Value.forValueNulls(Nulls.SKIP)) }
        .addModule(KotlinModule.Builder().build())
        .build()


fun<T> Map<String, *>.toObject(clazz: Class<T>): T? {
    if (this.isEmpty()) return null

    return try {
        objectMapper.convertValue(this, clazz)
    }
    catch (_: Exception) {
        null
    }
}
