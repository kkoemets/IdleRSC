package models.entities;

/** Index independent enums */
public enum SpellId implements Id {
  // list sorted by (latest) level requirement, if same first taken first released
  // suffixed _R when names collide, _R is for RETRO spell (before 24 May 2001)
  WIND_STRIKE(2),
  CONFUSE(5),
  WATER_STRIKE(8),
  ENCHANT_LVL1_AMULET(9),
  EARTH_STRIKE(11),
  WEAKEN(13),
  FIRE_STRIKE(15),
  BONES_TO_BANANAS(16),
  WIND_BOLT(17),
  CURSE(18),
  LOW_LEVEL_ALCHEMY(19),
  WATER_BOLT(20),
  VARROCK_TELEPORT(21),
  ENCHANT_LVL2_AMULET(22),
  EARTH_BOLT(23),
  LUMBRIDGE_TELEPORT(24),
  TELEKINETIC_GRAB(25),
  FIRE_BOLT(26),
  FALADOR_TELEPORT(27),
  CRUMBLE_UNDEAD(28),
  WIND_BLAST(29),
  SUPERHEAT_ITEM(30),
  CAMELOT_TELEPORT(31),
  WATER_BLAST(32),
  ENCHANT_LVL3_AMULET(33),
  IBAN_BLAST(34),
  ARDOUGNE_TELEPORT(35),
  EARTH_BLAST(36),
  HIGH_LEVEL_ALCHEMY(37),
  CHARGE_WATER_ORB(38),
  ENCHANT_LVL4_AMULET(39),
  WATCHTOWER_TELEPORT(40),
  FIRE_BLAST(41),
  CHARGE_EARTH_ORB(42),
  CLAWS_OF_GUTHIX(43),
  SARADOMIN_STRIKE(44),
  FLAMES_OF_ZAMORAK(45),
  WIND_WAVE(46),
  CHARGE_FIRE_ORB(47),
  WATER_WAVE(48),
  CHARGE_AIR_ORB(49),
  VULNERABILITY(50),
  ENCHANT_LVL5_AMULET(51),
  EARTH_WAVE(52),
  ENFEEBLE(53),
  FIRE_WAVE(54),
  STUN(55),
  CHARGE(56);

  private final int id;

  SpellId(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
