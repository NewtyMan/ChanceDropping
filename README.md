
# ChanceDropping (v0.1)

**ChanceDropping** is a Minecraft plugin that provides servers with the ability to add custom drops to any block in-game. It provides the functionality to add simple drops defined by material name or advanced ones by copying the item you are currently holding in your hand (with all the properties).

***Supported Server Version: 1.8 - 1.16.x***
&nbsp;

## Download & Install

Download the latest plugin release and drag it into your **plugins/** server folder. Make sure to delete any old version of this plugin first.

[ChanceDropping v.01](https://github.com/NewtyMan/ChanceDropping/releases)
&nbsp;
## Plugin features & TO-DO list

**Features:**
 - Add custom simple material item drop to a block
 - Add advanced item drop (with enchantments and custom quantity) by copying the item in your hand
 - List all custom drops for a certain block (if it has any)
 - Remove specific custom drops from a block

&nbsp;
**TO-DO:**
 - Custom drops for mobs
 - Inventory based graphical interface (alternative to command)
 - Backend code optimization
&nbsp;
## Config.yml

	error-invalid-syntax: "&6/chancedrop (add|remove|list) BLOCK (hand|BLOCK) (chance) [byte]"  
	error-invalid-block: "&c[ERROR] &fInvalid Block at Argument"  
	error-cannot-be-air: "&c[ERROR] &fBlock cannot be AIR"  
	error-invalid-chance: "&c[ERROR] &fInvalid Chance Amount"  
	error-invalid-index: "&c[ERROR] &fInvalid Item Index"  
	error-out-of-bound-index: "&c[ERROR] &fIndex Out Of Bounds"  
	  
	success-added-item: "&aSuccessfully added item to block"  
	success-removed-item: "&aSuccessfully removed item from block"  
	  
	list-format-text: "&c{index}: &e{item} &a({chance}%)"
Configuration file contains labels for different errors and chat messages that the plugin sends out. They can be fully customized and modified.

**list-format-text:** This entry supports three custom placeholders that the plugin changes to the associated value when listing the custom drops:

 - {index} => custom drop index (ex. 1, 2, 5)
 - {item} => custom drop material name (ex. GRASS_BLOCK, DIAMOND)
 - {chance} => chance for the drop to occur (ex. 20, 25, 5)

&nbsp;
## Commands & Permissions

**Syntax:**


*/chancedrop (add | remove | list) BLOCK (hand | DROP) (chance) (byte)*

**NOTICE:** The last parameter (byte) is only for server versions 1.8 - 1.12.2 since colored items such as wool and glass don't have separate materials for each color, but are all one material with different byte data.

**Example:**

***/chancedrop add GRASS_BLOCK DIAMOND 30***
Adds 30% chance to receive diamond upon breaking the grass block

***/chancedrop add GRASS_BLOCK WOOL 15 14***
Adds 15% chance to receive red wool block (byte data 14) when mining grass block

***/chancedrop add GRASS_BLOCK hand 30***
Adds 30% chance to receive the exact copy of the item that the player was holding when executing this command, when mining the grass block

***/chancedrop list GRASS_BLOCK***
Lists all custom drops defined for the grass block

***/chancedrop remove GRASS_BLOCK 1***
Removes the custom drop at index 1 from grass block (index can be found when using the **/chancedrop list** command)
**Permissions:**
***chancedrop.set***
Default: OP
Description: Provides permission to use the /chancedrop command

