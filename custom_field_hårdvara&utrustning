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
def fieldName = "Vad för hårdvara eller utrustning handlar det om?"
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
optionsMgr.createOption(fieldConfig, null, 1L, "Dator & tillbehör")
optionsMgr.createOption(fieldConfig, null, 2L, "Kablar")
optionsMgr.createOption(fieldConfig, null, 3L, "Konferenstelefon")
optionsMgr.createOption(fieldConfig, null, 4L, "Mobiltelefon")
optionsMgr.createOption(fieldConfig, null, 5L, "Kontorsstol")
optionsMgr.createOption(fieldConfig, null, 6L, "Projektor")
optionsMgr.createOption(fieldConfig, null, 7L, "Utskrift")
optionsMgr.createOption(fieldConfig, null, 8L, "Videokonferens")
optionsMgr.createOption(fieldConfig, null, 9L, "Videoutrustning")



log.warn("Custom field '$fieldName' skapades med alternativen: Dator & tillbehör, Kablar, Konferenstelefon, Mobiltelefon, Kontorsstol, Projektor, Utskrift, Videokonferens, Videoutrustning.")
