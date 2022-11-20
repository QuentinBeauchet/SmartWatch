-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : db
-- Généré le : jeu. 17 nov. 2022 à 02:30
-- Version du serveur : 10.9.4-MariaDB-1:10.9.4+maria~ubu2204
-- Version de PHP : 8.0.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `watch`
--

-- --------------------------------------------------------

--
-- Structure de la table `events`
--

CREATE TABLE IF NOT EXISTS `events` (
  `id` int(11) NOT NULL,
  `type_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `latitude` decimal(10,7) NOT NULL,
  `longitude` decimal(10,7) NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  `comment` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `events`
--

INSERT IGNORE INTO `events` (`id`, `type_id`, `user_id`, `latitude`, `longitude`, `date`, `comment`) VALUES
(1, 1, 1, '43.6205100', '6.9698400', '2022-11-16 03:14:22', "It\'s bigger than the dog O_o you might want to check it out"),
(3, 1, 4, '43.7101717', '7.2619517', '2022-11-17 02:20:04', 'Test Comment from Android Studio');

-- --------------------------------------------------------

--
-- Structure de la table `types`
--

CREATE TABLE IF NOT EXISTS `types` (
  `id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `icon` varchar(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `types`
--

INSERT IGNORE INTO `types` (`id`, `name`, `icon`) VALUES
(0, 'Localisation', '/assets/localisation.png'),
(1, 'Poop', '/assets/poop.png'),
(2, 'Worksite', '/assets/worksite.png'),
(3, 'Traffic jam', '/assets/traffic-jam.png');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL,
  `device_id` varchar(16) NOT NULL,
  `name` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT IGNORE INTO `users` (`id`, `device_id`, `name`) VALUES
(1, '00D861D8BB48', 'PC Quentin'),
(2, '35468C7E147D', 'Jane'),
(4, 'ced2e7e0cb52123a', 'sdk_gwear_x86');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY IF NOT EXISTS (`id`),
  ADD KEY IF NOT EXISTS `user_id` (`user_id`),
  ADD KEY IF NOT EXISTS `type_id` (`type_id`) USING BTREE;

--
-- Index pour la table `types`
--
ALTER TABLE `types`
  ADD PRIMARY KEY IF NOT EXISTS (`id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY IF NOT EXISTS (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `events`
--
ALTER TABLE `events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `types`
--
ALTER TABLE `types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `events`
--

ALTER TABLE `events`
  DROP CONSTRAINT IF EXISTS `events_ibfk_1`,
  DROP CONSTRAINT IF EXISTS `events_ibfk_2`;

ALTER TABLE `events`
  ADD CONSTRAINT `events_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `types` (`id`),
  ADD CONSTRAINT `events_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
