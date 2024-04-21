# Projet IRIAD : Prédiction des Notes des Élèves pour le Baccalauréat

Ce projet, baptisé IRIAD (Intelligence Artificielle pour la Prédiction des Notes des Élèves au Baccalauréat), vise à fournir une solution innovante pour prédire avec précision les performances des élèves lors des examens officiels, notamment le baccalauréat, dans le contexte du système éducatif algérien.

## Objectif

L'objectif principal de ce projet est de développer une application distribuée utilisant l'architecture JRMI (Java Remote Method Invocation) pour permettre une communication efficace entre les établissements scolaires et notre serveur. Cette communication permettra de collecter les données pertinentes, d'entraîner un modèle d'intelligence artificielle et de fournir des prédictions fiables sur les résultats attendus des élèves.

## Fonctionnalités

- Communication bidirectionnelle entre les établissements scolaires et le serveur pour l'envoi de données et la réception de prédictions.
- Prétraitement des données pour garantir leur qualité et cohérence avant l'entraînement du modèle.
- Entraînement du modèle d'intelligence artificielle à l'aide de techniques telles que les règles d'association pour prédire les performances des élèves.
- Gestion centralisée des connaissances acquises pour une réutilisation et un maintien à long terme.
- Interface client conviviale pour faciliter l'interaction avec le système.

## Structure du Projet

Le projet est organisé en plusieurs composants clés :

1. **Serveur :** Gère la réception des données, l'entraînement du modèle et la génération des prédictions.
2. **Client :** Permet l'envoi des données depuis les établissements scolaires vers le serveur et la réception des prédictions.
3. **Scripts Python :** Utilisés pour le prétraitement des données, l'entraînement du modèle et la génération des prédictions.
4. **Documentation :** Contient la documentation technique et l'explication des algorithmes utilisés.

## Installation et Utilisation

1. Cloner le dépôt GitHub sur votre machine locale.
2. Installer les dépendances nécessaires en exécutant `pip install -r requirements.txt`.
3. Exécuter le serveur en utilisant la commande `python server.py`.
4. Lancer l'application client pour envoyer les données et recevoir les prédictions.

## Contribution

Les contributions à ce projet sont les bienvenues. Vous pouvez contribuer en soumettant des pull requests pour améliorer le code, ajouter de nouvelles fonctionnalités ou corriger des bugs. Assurez-vous de suivre les meilleures pratiques de développement et de documenter vos modifications de manière appropriée.


---

N'hésitez pas à nous contacter pour toute question ou suggestion. Merci de votre intérêt pour le projet IRIAD !
