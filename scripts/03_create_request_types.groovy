import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.ProjectManager
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import com.atlassian.servicedesk.api.requesttype.CreateRequestTypeParameters
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager

def projectManager = ComponentAccessor.getProjectManager()
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def requestTypeService = ComponentAccessor.getOSGiComponentInstanceOfType(RequestTypeService)
def serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager)
def fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager)

def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser

// Hämta projekt och kontrollera service desk
def project = projectManager.getProjectObjByName("Felanmälan")
if (!project) {
    log.error "Projektet 'Felanmälan' hittades inte."
    return
}

def serviceDesk = serviceDeskManager.getServiceDeskByProjectId(project.id)
if (!serviceDesk) {
    log.error "'Felanmälan' är inte konfigurerad som en Service Desk."
    return
}

// Hämta issue type
def issueType = ComponentAccessor.constantsManager.allIssueTypeObjects.find { it.name == "Incident" }
if (!issueType) {
    log.error "Issue type 'Incident' hittades inte."
    return
}

// Hämta obligatoriska fält
def summaryField = ComponentAccessor.fieldManager.getField("summary")
def descriptionField = ComponentAccessor.fieldManager.getField("description")

// Lista över request types att skapa
def requestTypesToCreate = [
    [name: "Lokal",                          description: "Fel på t.ex. ventilation, belysning",                      fieldName: "Vad gäller ärendet? - Lokal"],
    [name: "Uppkoppling & Säkerhet",         description: "Problem med t.ex. nätverk, VPN eller lösenord",                          fieldName: "Vad gäller ärendet? - Uppkoppling & Säkerhet"],
    [name: "Hårdvara & Utrustning",          description: "Fel på t.ex. dator, skrivare, kablar",                              fieldName: "Vad gäller ärendet? - Hårdvara & Utrustning"],
    [name: "Handläggningssystem",            description: "Fel i system som t.ex. STIS, STELLA, EDH",                        fieldName: "Vad gäller ärendet? - Handläggningssystem"],
    [name: "Självservicekanaler",            description: "Fel på csn.se, Mina sidor eller Mina tjänster",                    fieldName: "Vad gäller ärendet? - Självservicekanaler"],
    [name: "Microsoft Office",               description: "Problem med t.ex. Office, Word, Outlook",                            fieldName: "Vad gäller ärendet? - Microsoft Office"],
    [name: "HR & Ekonomi",                   description: "Fel i t.ex. Primula, Proceedo, ERP",                          fieldName: "Vad gäller ärendet? - HR & Ekonomi"],
    [name: "Dokumentation & Ärendehantering",description: "Fel i t.ex. Confluence, Sharepoint, Mimer",                              fieldName: "Vad gäller ärendet? - Dokumentations & Ärendehantering"],
    [name: "Kommunikationsverktyg",          description: "Fel i t.ex. Skype, telefoni, ACE",                            fieldName: "Vad gäller ärendet? - Kommunikationsverktyg"]
]


requestTypesToCreate.each { rt ->
    def customField = customFieldManager.getCustomFieldObjects().find { it.name == rt.fieldName }
    if (!customField) {
        log.warn "Custom field '${rt.fieldName}' saknas - hoppar över ${rt.name}"
        return
    }

    // Kontrollera att fältet är kopplat till ärendetypen
    def config = fieldConfigSchemeManager.getFieldConfigSchemeForField(customField.id, issueType.id)
    if (!config) {
        log.error "Fältet '${rt.fieldName}' är inte kopplat till 'Incident' - hoppar över ${rt.name}"
        return
    }

    // Bygg fältlista
    def fieldIds = [summaryField.id, descriptionField.id, customField.id]*.toString()

    // Skapa request type
    def result = requestTypeService.createRequestType(
        currentUser,
        CreateRequestTypeParameters.builder()
            .withServiceDeskId(serviceDesk.id as Long)
            .withIssueTypeId(issueType.id)
            .withName(rt.name)
            .withDescription(rt.description)
            .withHelpText("Använd för: ${rt.description}")
            .withFieldIds(fieldIds)
            .build()
    )

    if (result.valid) {
        log.info " Request type '${rt.name}' skapad (ID: ${result.value.id})"
    } else {
        log.error "Misslyckades att skapa '${rt.name}': ${result.errorCollection}"
    }
}
