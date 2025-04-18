import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.servicedesk.api.portal.PortalGroupService
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import com.atlassian.servicedesk.api.requesttype.RequestType

def serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager)
def portalGroupService = ComponentAccessor.getOSGiComponentInstanceOfType(PortalGroupService)
def requestTypeService = ComponentAccessor.getOSGiComponentInstanceOfType(RequestTypeService)
def projectManager = ComponentAccessor.getProjectManager()

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

// Hämta alla portalgrupper
def portalGroups = portalGroupService.getPortalGroups(serviceDesk.get().id)

if (!portalGroups) {
    log.warn "Inga portalgrupper hittades för projektet."
    return
}

// Namn på grupper som ska matchas mot request types
def groupMapping = [
    "Arbetsplats & Fysisk miljö": ["Lokal", "Utrustning", "Kontor", "Arbetsmiljö"],
    "Stöd i arbetet": ["System", "Teknik"],
    "För kund & handläggare": ["Kundtjänst", "Handläggning"]
]

// Hämta alla request types för servicedesken
def requestTypes = requestTypeService.getRequestTypes(serviceDesk.get().id)

if (!requestTypes) {
    log.warn "Inga request types hittades för projektet."
    return
}

// Koppla request types till rätt portalgrupper baserat på namn
requestTypes.each { requestType ->
    groupMapping.each { groupName, keywords ->
        if (keywords.any { requestType.name.contains(it) }) {
            def portalGroup = portalGroups.find { it.name == groupName }

            if (portalGroup) {
                // Här kopplas request type till portalgruppen
                def updateResult = requestTypeService.updateRequestType(
                    new RequestTypeService.UpdateRequestTypeRequest.Builder(requestType.id)
                        .withPortalGroupIds([portalGroup.id])
                        .build(),
                    currentUser
                )

                if (updateResult.isValid()) {
                    log.info "Request type '${requestType.name}' kopplades till portalgrupp '${portalGroup.name}'"
                } else {
                    log.warn "Misslyckades koppla request type '${requestType.name}' till portalgrupp '${portalGroup.name}'"
                }
            } else {
                log.warn "Portalgrupp '${groupName}' hittades inte för request type '${requestType.name}'"
            }
        }
    }
}
