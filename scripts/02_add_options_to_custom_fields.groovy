import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.customfields.impl.SelectCFType
import com.atlassian.jira.project.ProjectManager

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def optionsManager = ComponentAccessor.getOptionsManager()
def projectManager = ComponentAccessor.getProjectManager()

// Hämta projektet "Felanmälan" (Projektet måste finnas redan)
def targetProject = projectManager.getProjectObjByName("Felanmälan")
if (!targetProject) {
    log.warn "Projektet 'Felanmälan' hittades inte – avslutar scriptet."
    return
}

// Fältnamn + tillhörande alternativ
def fieldOptionMap = [
    "Vad gäller ärendet? - Lokal" : ["Belysning", "Inpassering", "Kontorslokaler", "Kontorsstol", "Mötesrum", "Toaletter", "Ventilation"],
    "Vad gäller ärendet? - Uppkoppling & Säkerhet" : ["EFOS-kort", "Lösenord", "Nätverk", "VPN", "Webbläsare", "Wifi"],
    "Vad gäller ärendet? - Hårdvara & Utrustning" : ["Dator och tillhörande hårdvara", "Kablar", "Konferenstelefon", "Mobiltelefon och trådlöst headset", "Projektor", "Skrivare", "Utskrift", "Videokonferens", "Videoutrustning"],
    "Vad gäller ärendet? - Handläggningssystem" : ["Försättsblad skanning (EDH)", "Hemutrustningslån", "In- och utdata(central posthantering)", "Körkortslån", "STELLA Omstållningsstudiestöd", "STIS Ekonomi", "STIS Gemensamma system", "STIS Studiehjälp", "STIS Återbetalning"],
    "Vad gäller ärendet? - Självservicekanaler" : ["csn.se", "Mina sidor", "Mina tjänster"],
    "Vad gäller ärendet? - Microsoft Office" : ["Excel", "Office", "Outlook", "Powerpoint", "Word", "Wordmallar"],
    "Vad gäller ärendet? - HR & Ekonomi" : ["Antura", "ERP", "IA-system", "Läroportalen", "Primula", "Proceedo", "Reachmee"],
    "Vad gäller ärendet? - Dokumentations & Ärendehantering" : ["Confluence", "Jira", "Mimer", "Sharepoint", "Storegate"],
    "Vad gäller ärendet? - Kommunikationsverktyg" : ["ACE", "Skype", "Softphone", "Telefoni", "Touchpoint"]
]

fieldOptionMap.each { fieldName, optionsToAdd ->
    def field = customFieldManager.getCustomFieldObjects().find { it.name == fieldName }

    if (!field) {
        log.warn "Fältet '${fieldName}' finns inte – hoppar över."
        return
    }

    def fieldType = field.getCustomFieldType()
    if (!(fieldType?.class?.name?.contains("SelectCFType"))) {
        log.warn "Fältet '${fieldName}' är inte av typen 'Single Select' – hoppar över."
        return
    }

    def schemes = field.getConfigurationSchemes()
    if (schemes.isEmpty()) {
        log.warn "Fältet '${fieldName}' har inga konfigurationer – hoppar över."
        return
    }

    schemes.each { scheme ->
        scheme.configs.each { config ->
            def context = config.context
            def contextProjects = context.projectObjects

            // Vi vill bara lägga till alternativ för vårt målprojekt
            if (contextProjects.any { it.id == targetProject.id }) {
                def existingOptions = optionsManager.getOptions(config).collect { it.value }

                optionsToAdd.eachWithIndex { opt, idx ->
                    if (opt in existingOptions) {
                        log.info "Alternativet '${opt}' finns redan i '${fieldName}' för projektet 'Felanmälan'."
                    } else {
                        def newOpt = optionsManager.createOption(config, null, opt, idx + 1)
                        log.info "Alternativ '${opt}' tillagt i '${fieldName}' (ID: ${newOpt.optionId}) för projektet 'Felanmälan'"
                    }
                }
            } else {
                log.info "Kontexten för '${fieldName}' tillhör inte projektet 'Felanmälan' – hoppar över."
            }
        }
    }
}
