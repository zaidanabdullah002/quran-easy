# Quran API DTOs are parsed from server JSON and must keep stable field names in release.
-keep class com.zaidan.quraneasy.feature.quran.data.remote.dto.** { *; }

# Moshi relies on generic signatures and runtime annotations for adapter lookup.
-keepattributes Signature,RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault
