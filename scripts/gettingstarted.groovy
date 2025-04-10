// Detta skript hämtar alla projekt från Jira Service Management och filtrerar ut det projekt som matchar det angivna namnet.
// Om ett matchande projekt hittas, loggas projektets namn, nyckel och ID.

def projectNameToFind = "CSN" // Ändra detta till det projekt du söker efter

// Den här delen hämtar alla projekt från Jira Service Management API
def result = get("/rest/api/3/project")
        .asObject(List)

// Vi filtrerar projekten och loggar information om det projekt som matchar det specifika namnet
result.body.each { project ->
    if (project.name == projectNameToFind) {
        logger.info("Namn: ${project.name}, Nyckel: ${project.key}, ID: ${project.id}")
    }
}
return "Klart!"
