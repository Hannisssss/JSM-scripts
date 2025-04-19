import com.atlassian.jira.component.ComponentAccessor

// Hämtar CustomFieldManager
def customFieldManager = ComponentAccessor.getCustomFieldManager()

// Ange namnet på fältet du vill ta bort
def fieldName = "Mitt Exempelfält"

// Försök att hitta fältet med det angivna namnet
def customField = customFieldManager.getCustomFieldObjects().find { it.name == fieldName }

if (customField) {
    log.warn("Tar bort fältet '${customField.name}' (ID: ${customField.id})")
    customFieldManager.removeCustomField(customField)
    return "Fältet '${fieldName}' har tagits bort."
} else {
    return "Fältet '${fieldName}' hittades inte."
}

