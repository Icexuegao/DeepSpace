package ice.content

import ice.library.world.Load
import mindustry.content.Items
import singularity.type.AtomSchematic

object AtomSchematics: Load{
 var copper_schematic= AtomSchematic(Items.copper, 14000).apply{
      request.medium(0.23f)
      request.time(30f)
  }

  var lead_schematic= AtomSchematic(Items.lead, 14000).apply {
      request.medium(0.26f)
      request.time(30f)
  }

  var silicon_schematic= AtomSchematic(Items.silicon, 18000).apply {
      request.medium(0.41f)
      request.item(Items.sand, 1)
      request.time(45f)
  }

//  var titanium_schematic

//  var thorium_schematic

//  var uranium_schematic
//  var iridium_schematic
}