import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.CustomFieldManager
import com.atlassian.jira.issue.customfields.manager.FieldConfigSchemeManager
import com.atlassian.jira.issue.context.ProjectContext
import com.atlassian.jira.issue.context.JiraContextImpl
import com.atlassian.jira.project.ProjectManager

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager)
def projectManager = ComponentAccessor.getProjectManager()

// Hämta projektet "Felanmälan" med nyckel "FEL"
def project = projectManager.getProjectByCurrentKey("FEL")

if (!project) {
    log.error "Projektet 'Felanmälan' hittades inte."
    return
}

// Skapa kontext för projektet
def projectContext = new ProjectContext(project.id)

// Kontrollera om fältet redan finns
def existingField = customFieldManager.getCustomFieldObjects().find {
    it.name == "Vad gäller ärendet?"
}

if (existingField) {
    log.warn "Fältet 'Vad gäller ärendet?' finns redan med ID: ${existingField.id}"
    return
}

// Skapa custom field i projektets kontext
def newField = customFieldManager.createCustomField(
    "Vad gäller ärendet?",                        // Namn på fältet
    "Specificering av lokalrelaterat problem",     // Beskrivning (visas i Admin)
    customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:select"), // Typ: Single Select
    customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:selectsearcher"), // Sökningsstöd
    [projectContext] as Collection,                // Skapa kontexten för projektet "Felanmälan"
    []  // Field configuration schemes kan läggas till senare om du vill definiera specifika konfigurationer
)

log.info "Fältet 'Vad gäller ärendet?' har skapats med ID: ${newField.id} för projektet 'Felanmälan'"
