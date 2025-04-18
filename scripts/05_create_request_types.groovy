
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.ProjectManager
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import com.atlassian.servicedesk.api.requesttype.CreateRequestTypeParameters
import com.atlassian.servicedesk.api.util.paging.SinglePage
import com.atlassian.servicedesk.api.requesttype.RequestTypeField
import com.atlassian.servicedesk.api.organization.OrganizationService
import com.atlassian.servicedesk.api.util.paging.LimitedPagedRequest
import com.atlassian.jira.issue.fields.CustomField

def projectManager = ComponentAccessor.getProjectManager()
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def requestTypeService = ComponentAccessor.getOSGiComponentInstanceOfType(RequestTypeService)

def currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def project = projectManager.getProjectObjByName("Felanmälan")
if (!project) {
    log.error "Projektet 'Felanmälan' hittades inte."
    return
}

// Här listar vi de request types du vill skapa, med fältnamn som ska associeras
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

// Hämta issue type "Incident"
def issueType = ComponentAccessor.getConstantsManager().allIssueTypeObjects.find { it.name == "Incident" }
if (!issueType) {
    log.error "Issue type 'Incident' hittades inte."
    return
}

// Skapa varje request type
requestTypesToCreate.each { rt ->
    def customField = customFieldManager.getCustomFieldObjects().find { it.name == rt.fieldName }
    if (!customField) {
        log.warn "Custom field '${rt.fieldName}' hittades inte – hoppar över ${rt.name}."
        return
    }

    def result = requestTypeService.createRequestType(
        currentUser,
        CreateRequestTypeParameters.builder()
            .withProjectId(project.id)
            .withIssueTypeId(issueType.id)
            .withName(rt.name)
            .withDescription(rt.description)
            .withHelpText("Använd denna kategori för ${rt.description.toLowerCase()}.")
            .withFieldIds([RequestTypeField.SUMMARY, customField.id]) // Visa fälten i portalen
            .build()
    )

    if (result.isValid()) {
        log.info "Request type '${rt.name}' skapad."
    } else {
        log.error "Kunde inte skapa request type '${rt.name}': ${result.getErrorCollection()}"
    }
}
