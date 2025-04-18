import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.CustomFieldManager
import com.atlassian.jira.issue.customfields.manager.FieldConfigSchemeManager
import com.atlassian.jira.issue.customfields.impl.SelectCFType
import com.atlassian.jira.issue.context.GlobalIssueContext
import com.atlassian.jira.issue.context.JiraContextImpl

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager)

// Kontrollera om fältet redan finns
def existingField = customFieldManager.getCustomFieldObjects().find {
    it.name == "Vad gäller ärendet?"
}

if (existingField) {
    log.warn "Fältet 'Vad gäller ärendet?' finns redan med ID: ${existingField.id}"
    return
}

// Skapa custom field i global kontext (eller justera till en specifik kontext om det behövs)
def jiraContext = new JiraContextImpl(GlobalIssueContext.getInstance())

def newField = customFieldManager.createCustomField(
    "Vad gäller ärendet?",                        // Namn på fältet
    "Specificering av lokalrelaterat problem",     // Beskrivning (visas i Admin)
    customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:select"), // Typ: Single Select
    customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:selectsearcher"), // Sökningsstöd
    [jiraContext] as Collection,                   // Kontexter
    []  // Field configuration schemes kan läggas till senare om du vill definiera specifika konfigurationer
)

log.info "Fältet 'Vad gäller ärendet?' har skapats med ID: ${newField.id}"
