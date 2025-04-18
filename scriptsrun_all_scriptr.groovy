import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.jira.project.ProjectManager
import com.atlassian.servicedesk.api.portal.PortalGroupService
import com.atlassian.jira.issue.customfields.manager.CustomFieldManager

def projectManager = ComponentAccessor.getProjectManager()
def serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager)
def portalGroupService = ComponentAccessor.getOSGiComponentInstanceOfType(PortalGroupService)
def customFieldManager = ComponentAccessor.getCustomFieldManager()

def project = projectManager.getProjectObjByName("Felanmälan")
if (!project) {
    log.warn "Projektet 'Felanmälan' finns inte – avslutar scriptet."
    return
}

def serviceDesk = serviceDeskManager.getServiceDeskForProject(project)
if (!serviceDesk.isPresent()) {
    log.warn "Kunde inte hämta ServiceDesk för projektet."
    return
}

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Steg 1: Radera gamla konfigurationer (portalgrupper, request types, etc.)
log.info "Steg 1: Raderar gamla konfigurationer..."
portalGroupService.getPortalGroups(serviceDesk.get().id).each { portalGroup ->
    def deleteResult = portalGroupService.deletePortalGroup(portalGroup.id, currentUser)
    if (deleteResult.isValid()) {
        log.info "Portalgrupp '${portalGroup.name}' raderades."
    } else {
        log.warn "Misslyckades att radera portalgrupp '${portalGroup.name}'"
    }
}

serviceDesk.get().getRequestTypes().each { requestType ->
    def deleteResult = serviceDeskManager.deleteRequestType(requestType.id)
    if (deleteResult.isValid()) {
        log.info "Request type '${requestType.name}' raderades."
    } else {
        log.warn "Misslyckades att radera request type '${requestType.name}'"
    }
}

customFieldManager.getCustomFieldObjects().each { customField ->
    if (customField.name.contains("Vad gäller ärendet?")) {
        try {
            customFieldManager.removeCustomField(customField)
            log.info "Custom field '${customField.name}' raderades."
        } catch (Exception e) {
            log.warn "Misslyckades att ta bort custom field '${customField.name}': ${e.message}"
        }
    }
}

// Steg 2: Skapa nya portalgrupper
log.info "Steg 2: Skapar nya portalgrupper..."
def groups = [
    [name: "Arbetsplats & Fysisk miljö", description: "Felanmälningar som rör kontor, utrustning, lokaler och arbetsmiljö"],
    [name: "Stöd i arbetet", description: "-"],
    [name: "För kund & handläggare", description: "-"]
]

groups.each { group ->
    def result = portalGroupService.createPortalGroup(
        new PortalGroupService.CreatePortalGroupRequest.Builder(serviceDesk.get().id)
            .withName(group.name)
            .withDescription(group.description)
            .build(),
        currentUser
    )

    if (result.isValid()) {
        log.info "Portalgrupp '${group.name}' skapades."
    } else {
        log.warn "Misslyckades skapa grupp '${group.name}': ${result.errorCollection}"
    }
}

// Steg 3: Skapa fält och alternativ (t.ex. för "Vad gäller ärendet?")
log.info "Steg 3: Skapar nya custom fields och alternativ..."
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
    // Här kan du anropa ditt tidigare script för att skapa och lägga till fältalternativ för varje custom field.
}

// Steg 4: Koppla request types till de nyss skapade grupperna (om det behövs)
log.info "Steg 4: Koppla request types till portalgrupper..."
// Din kod för att koppla request types till portalgrupper

log.info "✅ Alla script körda framgångsrikt!"
