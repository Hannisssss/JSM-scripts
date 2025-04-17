import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.fields.config.FieldConfig
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.customfields.manager.CustomFieldManager
import com.atlassian.jira.issue.customfields.impl.SelectCFType
import com.atlassian.jira.issue.context.GlobalIssueContext
import com.atlassian.jira.issue.context.JiraContextImpl
import com.atlassian.jira.issue.context.ProjectContext
import com.atlassian.jira.issue.context.JiraContextNode
import com.atlassian.jira.issue.customfields.manager.FieldConfigSchemeManager

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def fieldConfigSchemeManager = ComponentAccessor.getComponent(FieldConfigSchemeManager)
def jiraContext = new JiraContextImpl(GlobalIssueContext.getInstance())

// Kontrollera om fältet redan finns
def existingField = customFieldManager.getCustomFieldObjects().find {
    it.name == "Vad gäller ärendet?"
}

if (existingField) {
    log.warn "Fältet 'Vad gäller ärendet?' finns redan med ID: ${existingField.id}"
    return
}

// Skapa custom field
def newField = customFieldManager.createCustomField(
    "Vad gäller ärendet?",                        // Namn på fältet
    "Specificering av lokalrelaterat problem", // Beskrivning (visas i Admin)
    customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:select"), // Typ: Single Select
    customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:selectsearcher"), // Sökningsstöd
    [jiraContext] as Collection<JiraContextNode>,
    [] // Field configuration schemes kan läggas till senare
)

log.info "Fältet 'Vad gäller ärendet?' har skapats med ID: ${newField.id}"
