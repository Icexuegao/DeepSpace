const lib = require("base/coflib");

let itemBridge = new ItemBridge("装甲传送带桥");
itemBridge.instantTransfer = true;

lib.setBuilding(ItemBridge.ItemBridgeBuild, itemBridge, {
    invert: false,
    sortItem: null,
    acceptItem(source, item) {
        if (this.link == -1) {
            let to = this.getTileTarget(item, source, false);
            return to != null && to.acceptItem(this, item) && to.team == this.team;
        } else return this.super$acceptItem(source, item);
    },
    handleItem(source, item) {
        if (this.link == -1) this.getTileTarget(item, source, true).handleItem(this, item);
        else this.super$handleItem(source, item);
    },
    isSame(other) {
        return other != null && other.block.instantTransfer;
    },
    getTileTarget(item, source, flip) {
        let dir = source.relativeTo(this.tile.x, this.tile.y);
        if (dir == -1) return null;
        let to;
        if (((item == this.sortItem) != this.invert) == this.enabled) {
            if (this.isSame(source) && this.isSame(this.nearby(dir))) return null;
            to = this.nearby(dir);
        } else {
            let a = this.nearby(Mathf.mod(dir - 1, 4));
            let b = this.nearby(Mathf.mod(dir + 1, 4));
            let ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) && a.acceptItem(this, item);
            let bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) && b.acceptItem(this, item);
            if (ac && !bc) to = a;
            else if (bc && !ac) to = b;
            else if (!bc) return null;
            else {
                to = (this.rotation & (1 << dir)) == 0 ? a : b;
                if (flip) this.rotation ^= (1 << dir);
            }
        }
        return to
    },
    buildConfiguration(table) {
        if (this.link == -1) {
            ItemSelection.buildTable(table, Vars.content.items(), prov(() => this.sortItem), cons(item => this.sortItem = item), this.block.selectionColumns);
        }
    },
    write(write) {
        this.super$write(write);
        write.s(this.sortItem == null ? -1 : this.sortItem.id);
    },
    read(read, revision) {
        this.super$read(read, revision);
        this.sortItem = Vars.content.item(read.s());
    }
})


/*let liquidBridge = new LiquidBridge("装甲导管桥");
liquidBridge.instantTransfer = true;

lib.setBuilding(LiquidBridge.LiquidBridgeBuild, liquidBridge, {
	invert: false,
	sortLiquid: null,
	acceptLiquid(source, liquid) {
		if (this.link == -1) {
			let to = this.getTileTarget(liquid, source, false);
			return to != null && to.acceptLiquid(this, liquid) && to.team == this.team;
		} else return this.super$acceptLiquid(source, liquid);
	},
	handleLiquid(source, liquid, amount) {
		if (this.link == -1) this.getTileTarget(liquid, source, true).handleLiquid(this, liquid, amount;
		else this.super$handleLiquid(source, liquid, amount);
	},
	isSame(other) {
		return other != null && other.block.instantTransfer;
	},
	getTileTarget(liquid, source, flip) {
		let dir = source.relativeTo(this.tile.x, this.tile.y);
		if (dir == -1) return null;
		let to;
		if (((liquid == this.sortLiquid) != this.invert) == this.enabled) {
			if (this.isSame(source) && this.isSame(this.nearby(dir))) return null;
			to = this.nearby(dir);
		} else {
			let a = this.nearby(Mathf.mod(dir - 1, 4));
			let b = this.nearby(Mathf.mod(dir + 1, 4));
			let ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) && a.acceptLiquid(this, liquid);
			let bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) && b.acceptLiquid(this, liquid);
			if (ac && !bc) to = a;
			else if (bc && !ac) to = b;
			else if (!bc) return null;
			else {
				to = (this.rotation & (1 << dir)) == 0 ? a : b;
				if (flip) this.rotation ^= (1 << dir);
			}
		}
		return to
	},
	buildConfiguration(table) {
		if (this.link == -1) {
			ItemSelection.buildTable(table, Vars.content.liquid(), prov(() => this.sortLiquid), cons(liquid => this.sortLiquid = liquid), this.block.selectionColumns);
		}
	},
	write(write) {
		this.super$write(write);
		write.s(this.sortLiquid == null ? -1 : this.sortLiquid.id);
	},
	read(read, revision) {
		this.super$read(read, revision);
		this.sortLiquid = Vars.content.liquid(read.s());
	}
})*/