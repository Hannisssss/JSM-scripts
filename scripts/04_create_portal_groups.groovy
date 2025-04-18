import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.ServiceDeskManager
import com.atlassian.servicedesk.api.organization.OrganizationsService
import com.atlassian.servicedesk.api.portal.PortalGroupService
import com.atlassian.servicedesk.api.util.paging.SimplePagedRequest
import com.atlassian.servicedesk.api.util.paging.PagedResponse

def projectManager = ComponentAccessor.getProjectManager()
def serviceDeskManager = ComponentAccessor.getOSGiComponentInstanceOfType(ServiceDeskManager)
def portalGroupService = ComponentAccessor.getOSGiComponentInstanceOfType(PortalGroupService)

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

// Namn + beskrivning för grupper
def groups = [
    [name: "Arbetsplats & Fysisk miljö", description: "Felanmälningar som rör kontor, utrustning, lokaler och arbetsmiljö"],
    [name: "Stöd i arbetet", description: "-"],
    [name: "För kund & handläggare", description: "-"]
]

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

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
