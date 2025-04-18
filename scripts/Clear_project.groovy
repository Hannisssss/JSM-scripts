
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.issue.customfields.manager.CustomFieldManager
import com.atlassian.servicedesk.api.portal.PortalGroupService

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

// Ta bort alla portalgrupper
portalGroupService.getPortalGroups(serviceDesk.get().id).each { portalGroup ->
    def deleteResult = portalGroupService.deletePortalGroup(portalGroup.id, currentUser)
    if (deleteResult.isValid()) {
        log.info "Portalgrupp '${portalGroup.name}' raderades."
    } else {
        log.warn "Misslyckades att radera portalgrupp '${portalGroup.name}'"
    }
}

// Ta bort alla request types
serviceDesk.get().getRequestTypes().each { requestType ->
    def deleteResult = serviceDeskManager.deleteRequestType(requestType.id)
    if (deleteResult.isValid()) {
        log.info "Request type '${requestType.name}' raderades."
    } else {
        log.warn "Misslyckades att radera request type '${requestType.name}'"
    }
}

// Ta bort alla custom fields relaterade till projektet
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
