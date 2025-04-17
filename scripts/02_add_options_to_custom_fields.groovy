// Fältnamn + tillhörande alternativ
def fieldOptionMap = [
    "Vad gäller ärendet? - Lokal"        : ["Belysning", "Inpassering", "Kontorslokaler", "Kontorsstol", "Mötesrum", "Toaletter", "Ventilation"],
    "Vad gäller ärendet? - Uppkoppling & säkerhet": ["EFOS-kort", "Lösenord", "Nätverk", "VPN", "Webbläsare", "Wifi"],
    "Vad gäller ärendet? - Hårdvara & Utrustning": ["Dator och tillhörande hårdvara", "Kablar", "Konferenstelefon", "Mobiltelefon och trådlöst headset", "Projektor", "Skrivare", "Utskrift", "Videokonferens", "Videoutrustning"],
    "Vad gäller ärendet? - Handläggningssystem": ["Försättsblad skanning (EDH)", "Hemutrustningslån", "In- och utdata(central posthantering)", "Körkortslån", "STELLA Omstållningsstudiestöd", "STIS Ekonomi", "STIS Gemensamma system", "STIS Studiehjälp", "STIS Återbetalning"],
    "Vad gäller ärendet? - Självservicekanaler" : ["csn.se", "Mina sidor", "Mina tjänster"],
    "Vad gäller ärendet? - Microsoft Office" : ["Excel", "Office", "Outlook", "Powerpoint", "Word", "Wordmallar"],

]

fieldOptionMap.each { fieldName, optionsToAdd ->
    def field = customFieldManager.getCustomFieldObjects().find { it.name == fieldName }
    if (!field) {
        log.warn "Fältet '${fieldName}' finns inte – hoppar över."
        return
    }

    def config = field.getRelevantConfig(null)
    if (!config) {
        log.warn "FieldConfig saknas för '${fieldName}' – hoppar över."
        return
    }

    def existing = optionsManager.getOptions(config).collect { it.value }

    optionsToAdd.eachWithIndex { opt, idx ->
        if (opt in existing) {
            log.info "Alternativet '${opt}' finns redan för '${fieldName}'."
        } else {
            def newOpt = optionsManager.createOption(config, null, opt, idx + 1)
            log.info "Alternativ '${opt}' tillagt i '${fieldName}' (ID: ${newOpt.optionId})"
        }
    }
}
