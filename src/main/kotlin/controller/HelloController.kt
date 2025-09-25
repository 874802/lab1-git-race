package es.unizar.webeng.hello.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime
import java.util.*

@Controller
class HelloController(
    @param:Value("\${app.message:Hello World}") 
    private val message: String,
    private val messageSource: MessageSource
) {
    
    private fun getTimeBasedGreeting(locale: Locale): String {
        val currentHour = LocalTime.now().hour
        return when (currentHour) {
            in 5..11 -> messageSource.getMessage("greeting.morning", null, locale)
            in 12..16 -> messageSource.getMessage("greeting.afternoon", null, locale)
            in 17..21 -> messageSource.getMessage("greeting.evening", null, locale)
            else -> messageSource.getMessage("greeting.night", null, locale)
        }
    }
    
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "en") lang: String,
        locale: Locale
    ): String {
        val currentLocale = when (lang) {
            "es" -> Locale.forLanguageTag("es")
            "fr" -> Locale.forLanguageTag("fr")
            else -> Locale.ENGLISH
        }
        val timeGreeting = getTimeBasedGreeting(currentLocale)
        val worldText = messageSource.getMessage("greeting.world", null, currentLocale)
        val greeting = if (name.isNotBlank()) "$timeGreeting, $name!" else "$timeGreeting, $worldText!"
        
        model.addAttribute("message", greeting)
        model.addAttribute("name", name)
        model.addAttribute("currentLang", currentLocale.language)
        model.addAttribute("availableLanguages", mapOf(
            "en" to "English",
            "es" to "Español", 
            "fr" to "Français"
        ))
        return "welcome"
    }
}

@RestController
class HelloApiController(
    private val messageSource: MessageSource
) {
    
    private fun getTimeBasedGreeting(locale: Locale): String {
        val currentHour = LocalTime.now().hour
        return when (currentHour) {
            in 5..11 -> messageSource.getMessage("greeting.morning", null, locale)
            in 12..16 -> messageSource.getMessage("greeting.afternoon", null, locale)
            in 17..21 -> messageSource.getMessage("greeting.evening", null, locale)
            else -> messageSource.getMessage("greeting.night", null, locale) // Add this line
        }
    }
    
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(
        @RequestParam(defaultValue = "World") name: String,
        @RequestParam(defaultValue = "en") lang: String,
        locale: Locale
    ): Map<String, String> {
        val currentLocale = when (lang) {
            "es" -> Locale.forLanguageTag("es")
            "fr" -> Locale.forLanguageTag("fr")
            else -> Locale.ENGLISH
        }
        val timeGreeting = getTimeBasedGreeting(currentLocale)
        val worldText = if (name == "World") messageSource.getMessage("greeting.world", null, currentLocale) else name
        
        return mapOf(
            "message" to "$timeGreeting, $worldText!",
            "timestamp" to java.time.Instant.now().toString()
        )
    }
}