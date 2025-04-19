import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.context.GlobalIssueContext
import com.atlassian.jira.issue.customfields.CustomFieldType
import com.atlassian.jira.issue.customfields.CustomFieldSearcher

def customFieldManager = ComponentAccessor.customFieldManager

def fieldName = "Mitt Exempelfält"
def description = "Ett exempel på ett custom field"

// Hämta typerna via customFieldManager
def fieldType = customFieldManager.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:select")
def fieldSearcher = customFieldManager.getCustomFieldSearcher("com.atlassian.jira.plugin.system.customfieldtypes:multiselectsearcher")

// Kontrollera om fältet redan finns
def existingField = customFieldManager.getCustomFieldObjectsByName(fieldName).find()
if (existingField) {
    log.warn("Fältet '$fieldName' finns redan")
    return
}

// Skapa fältet med global kontext (hela Jira) och alla issue types
def customField = customFieldManager.createCustomField(
    fieldName,
    description,
    fieldType,
    fieldSearcher,
    [GlobalIssueContext.getInstance()], // Kontext: globalt
    null // Alla issue types
)

log.warn("Custom field '$fieldName' har skapats.")
