# Kom igång med Jira API och ScriptRunner

Denna dokumentation hjälper dig att komma igång med att automatisera processer i Jira via **ScriptRunner** och **Jira API**.

## Förutsättningar

För att kunna använda dessa skript behöver du följande:
1. En aktiv Jira-instans.
2. **ScriptRunner** installerat på din Jira-instans. Om du inte har ScriptRunner installerat, följ [denna guide](https://marketplace.atlassian.com/apps/6820/scriptrunner-for-jira) för att installera det.
3. En **API-nyckel** för att kunna autentisera API-förfrågningar. Följ denna [länk för att skapa en API-nyckel i Jira](https://id.atlassian.com/manage-profile/security/api-tokens).

## Steg 1: Hämta alla Jira-projekt och deras ID

För att interagera med Jira-projekt behöver du hämta deras ID:n. Använd följande skript för att hämta alla projekt och deras ID.

### [getAllProjects.groovy](Scripts/get_projects.groovy)

Detta skript hämtar alla projekt i din Jira-instans och skriver ut deras namn, nyckel och ID till loggen. Du kan använda dessa ID:n för att skapa, uppdatera eller ta bort projekt.

För att köra skriptet:
1. Gå till **ScriptRunner** i Jira.
2. Klistra in skriptet i ScriptRunner's script-console.
4. Kör skriptet för att få en lista på alla dina Jira-
projekt.
![Namnlös](https://github.com/user-attachments/assets/3d825575-aa65-45cf-8de9-e87ffbcc7586)

## Steg 2: Lägg till API-nyckel för autentisering
För att autentisera API-förfrågningar behöver du en API-nyckel. Följ denna länk till Atlassians dokumentation för att skapa en API-nyckel och använd den i dina skript.
