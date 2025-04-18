import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.context.manager.FieldConfigSchemeManager
import com.atlassian.jira.issue.context.JiraContextImpl
import com.atlassian.jira.issue.context.JiraContextNode
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.issue.issuetype.IssueType

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager)
def projectManager = ComponentAccessor.getProjectManager()
def constantsManager = ComponentAccessor.getConstantsManager()

def project = projectManager.getProjectObjByName("Felanmälan")
if (!project) {
    log.error "Projektet 'Felanmälan' kunde inte hittas."
    return
}

def issueType = constantsManager.getAllIssueTypeObjects().find { it.name == "Incident" }
if (!issueType) {
    log.error "Issue type 'Incident' kunde inte hittas."
    return
}

def existingField = customFieldManager.getCustomFieldObjectByName("Vad gäller ärendet?")
if (existingField) {
    log.warn "Fältet '${existingField.name}' finns redan (ID: ${existingField.id})"
    return
}

def newField = customFieldManager.createCustomField(
    "Vad gäller ärendet?",
    "Specificering av lokalrelaterat problem",
    customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:select"),
    customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:selectsearcher"),
    [new JiraContextImpl(project)] as Collection<JiraContextNode>,
    [issueType.id] as Collection<String>
)

log.info "Fält skapat: ${newField.name} (ID: ${newField.id}) för projektet ${project.name} och issue type ${issueType.name}"
