import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.context.GlobalIssueContext
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.fields.config.FieldConfig

// Managers
def cfMgr = ComponentAccessor.customFieldManager
def optionsMgr = ComponentAccessor.getComponent(OptionsManager)
def schemeMgr = ComponentAccessor.getComponent(FieldConfigSchemeManager)

// Fältnamn
def fieldName = "Vilket verktyg inom HR & Ekonomi?"
def description  = "" 

// Kolla om fältet redan finns
if (cfMgr.getCustomFieldObjectsByName(fieldName)) {
    log.warn("Fältet '$fieldName' finns redan.")
    return
}

// Hämta fälttyp
def fieldType = cfMgr.getCustomFieldType("com.atlassian.jira.plugin.system.customfieldtypes:select")
def customField = cfMgr.createCustomField(
    fieldName, 
    description, 
    fieldType, 
    null, 
    [GlobalIssueContext.getInstance()], 
    null
)

// Hämta FieldConfig
def fieldConfig = customField.getRelevantConfig(GlobalIssueContext.getInstance())

// Om fieldConfig är null, skapa en ny konfiguration
if (!fieldConfig) {
    fieldConfig = schemeMgr.createDefaultScheme(customField, [GlobalIssueContext.getInstance()]).getOneAndOnlyConfig()
}

// Skapa alternativ
optionsMgr.createOption(fieldConfig, null, 1L, "Antura")
optionsMgr.createOption(fieldConfig, null, 2L, "ERP")
optionsMgr.createOption(fieldConfig, null, 3L, "Primula")
optionsMgr.createOption(fieldConfig, null, 4L, "Proceedo")
optionsMgr.createOption(fieldConfig, null, 5L, "Reachmee")



log.warn("Custom field '$fieldName' skapades med alternativen: Antura, ERP, Primula, Proceedo, Reachmee")
