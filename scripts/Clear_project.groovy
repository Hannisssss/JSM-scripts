import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.portal.PortalGroupService

def portalGroupService = ComponentAccessor.getOSGiComponentInstanceOfType(PortalGroupService)
def projectManager = ComponentAccessor.getProjectManager()
def serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager)

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

// Radera gamla portalgrupper
portalGroupService.getPortalGroups(serviceDesk.get().id).each { portalGroup ->
    portalGroupService.deletePortalGroup(portalGroup.id, ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser())
    log.info "Portalgrupp '${portalGroup.name}' raderades."
}

log.info "Steg 1: Gamla konfigurationer raderade."
