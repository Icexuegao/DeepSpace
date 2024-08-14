/**
 * by pankiwi © 2021
 * you can use, edit, here but not remove this
 * thanks contributors
 **/

/*
 * Registers a unit's class for I/O stuff (saves and net)
 * Requires unit.constructor.get() to have classId: () => unit.classId
 */
let registerClass = unit => {
    // Register unit's name
    EntityMapping.nameMap.put(unit.name, unit.constructor);
    // Find available class id and register it
    unit.classId = -1;
    for (var i in EntityMapping.idMap) {
        if (!EntityMapping.idMap[i]) {
            EntityMapping.idMap[i] = unit.constructor;
            unit.classId = i;
            return;
        }
    }
    // Incase you used up all 256 class ids; use the same code for ~250 units you idiot.
    throw new IllegalArgumentException(unit.name + " has no class ID");
};

const logLib = (type, string, obj) => {
    let libraryV = "1.0";
    let t = Log.LogLevel.none;
    switch (type) {
        case "i":
            t = Log.LogLevel.info;
            break;
        case "w":
            t = Log.LogLevel.warn;
            break;
        case "e":
            t = Log.LogLevel.err;
            break;
    }

    Log.log(t, "Library snake " + "[accent]" + ("(" + libraryV + ")") + "[] : " + string, obj != null ? obj : []);
}

module.exports = {
    segemntAI: () => extend(AIController, {
        updateMovement() {
            this.unloadPayloads();
            // check segment
            if (this.unit.getSegment() != null && !this.unit.getSegment().dead) {
                // get unit
                let next = this.unit.getSegment();
                //get dst
                let dst = this.unit.getDstSegment();
                // calculated pos
                Tmp.v1.trns(Angles.angle(this.unit.x, this.unit.y, next.x, next.y), -this.unit.getOffset());
                // check dst > offset
                if (dst > this.unit.getOffset()) {
                    // move unit
                    Tmp.v2.trns(
                        Angles.angle(this.unit.x, this.unit.y, next.x + Tmp.v1.x, next.y + Tmp.v1.y),
                        this.unit.speed());
                    this.unit.moveAt(Tmp.v2);
                }
            }
        }
    }),
    // sement for sneak
    segment(name, type, constructor) {
        // check is null constructor
        if (type == undefined) type = {};
        if (constructor == undefined) constructor = {};
        // set code default
        type = Object.assign({
            flying: true,
            offsetSegment: 10,
            engines: 0,
            engineSize: 0,
            engineRotOffset: 0,
            drawEngine(unit) {
                if (!unit.isFlying()) return;
                var scl = unit.elevation;
                var offset = unit.type.engineOffset / 2 + unit.type.engineOffset / 2 * scl;
                Draw.color(unit.team.color);
                for (let i = 0; i < this.engines; i++) {
                    Fill.circle(
                        unit.x + Angles.trnsx(unit.rotation + this.engineRotOffset + (i * 360 / this.engines), offset),
                        unit.y + Angles.trnsy(unit.rotation + this.engineRotOffset + (i * 360 / this.engines), offset), (unit.type.engineSize + Mathf.absin(Time.time, 2, unit.type.engineSize / 4)) * scl);
                }
                Draw.color(Color.white);
                for (let i = 0; i < this.engines; i++) {
                    Fill.circle(
                        unit.x + Angles.trnsx(unit.rotation + this.engineRotOffset + (i * 360 / this.engines), offset - 1),
                        unit.y + Angles.trnsy(unit.rotation + this.engineRotOffset + (i * 360 / this.engines), offset - 1), (unit.type.engineSize + Mathf.absin(Time.time, 2, unit.type.engineSize / 4)) / 2 * scl);
                }
                Draw.color();
            },
            getOffset() {
                return this.offsetSegment;
            }
        }, type);
        let unit = extend(UnitType, name, type);
        // code segment
        unit.constructor = () => extend(UnitEntity, Object.assign({
            _offset: 0,
            _parent: null,
            _segment: null,
            // it only read unit
            idParent: -1,
            idSegment: -1,
            setType(type) {
                this._offset = type.getOffset();
                this.super$setType(type);
            },
            update() {
                this.super$update();
                // custom update
                this._update();
            },
            _update() {
                // find head and segment
                if (this._parent == null || this._segment == null) this.findFamily();
                // get parent
                let parent = this.getParent();
                // kill if paren is null
                if (parent == null || parent.dead) this.kill();
                // update team
                if (parent != null && this.team != parent.team) this.team = parent.team;
            },
            findFamily() {
                // get units
                let segment = Groups.unit.getByID(this.idSegment);
                let parent = Groups.unit.getByID(this.idParent);
                // check segment
                if (segment != null && segment.id != null) this.setSegment(segment);
                // check parent
                if (parent != null && parent.id != null) {
                    // set parent
                    this.setParent(parent);
                    // call head for add child
                    this.getParent().addChild(this.self);
                    // update head if segment is null
                    if (segment == null) this.getParent()._update();
                } else {
                    this.kill();
                    logLib("e", "parent not can load");
                }
            },
            // return dinstace of next segment
            getDstSegment() {
                let next = this.getSegment();
                if (next == null || next.dead) return -100;
                Tmp.v1.trns(Angles.angle(this.x, this.y, next.x, next.y), -unit.offsetSegment);
                return Mathf.dst(this.x, this.y, next.x + Tmp.v1.x, next.y + Tmp.v1.y) - (this.hitSize + 10);
            },
            isAI() {
                return false; // you can't control go brrrrrrr
            },
            // as the next segment moves further away, it will have higher speed
            speed() {
                if (this._parent != null) {
                    return this.getParent().speed() + ((this.getDstSegment()) / (unit.offsetSegment + this.hitSize));
                } else {
                    this.super$speed();
                    logLib("w", "speed normal");
                }
            },
            // fuck you pallarax
            impulseNet(vec) {
            },
            // can head create infinity segments
            cap() {
                return this.count() + 1;
            },
            getOffset() {
                return this._offset;
            },
            setParent(pr) {
                if (pr != null) {
                    this._parent = pr;
                    this.idParent = pr.id;
                } else logLib("w", "parent is null - " + pr);
            },
            getParent() {
                return this._parent;
            },
            setSegment(seg) {
                if (seg != null) {
                    this._segment = seg;
                    this.idSegment = seg.id;
                } else logLib("w", "segment is null - " + seg);
            },
            getSegment() {
                return this._segment;
            },
            write(write) {
                this.super$write(write);
                // write id
                write.i(this.id);
                // write id segment and parent
                write.i(this._segment != null && this._segment.id != null ? this._segment.id : -1);
                write.i(this._parent != null && this._parent.id != null ? this._parent.id : -1);
            },
            read(read) {
                this.super$read(read);
                // read id
                this.id = read.i();
                // read id and get unit from Groups by id
                this.idSegment = read.i();
                this.idParent = read.i();
            },
            // read for segment
            classId: () => unit.classId
        }, constructor));
        registerClass(unit);
        return unit;
    },
    head(name, type, constructor) {
        if (type == undefined) type = {};
        if (constructor == undefined) constructor = {};
        type = Object.assign({
            lengthSnake: 1,
            // body
            body: null,
            end: null,
            flying: true
        }, type);
        let unit = extend(UnitType, name, type);
        unit.constructor = () => extend(UnitEntity, Object.assign({
            _segments: [],
            totalSegments: 0,
            setSneak: false,
            timeOut: 1,
            tryFindSegment: false,
            add() {
                // create segments
                this.super$add();
                // check is on snake
                if (!this.setSneak) this.createSegments();
            },
            update() {
                this.super$update();
                // update
                this._update();
            },
            _update() {
                if (!this.tryFindSegment) this.timeOutSegment();
                // if save map on creating segments
                if (!this.setSneak) this.createSegments();
                // if has changes array segments
                if (this._segments.filter(unitSegment => unitSegment != null && !unitSegment.dead).length != this.totalSegments) {
                    // update
                    this._segments = this._segments.filter(unitSegment => unitSegment != null && !unitSegment.dead);
                    this.totalSegments = this._segments.length;
                    // update segments
                    for (let i = 0; i < this._segments.length; i++) {
                        let lastSegment = this._segments[i - 1] ? this._segments[i - 1] : this.self;
                        let segment = this._segments[i];
                        // set segment
                        if ((segment != null && !segment.dead) && (lastSegment != null && !lastSegment.dead)) segment.setSegment(lastSegment);
                    }
                }
                // detroy head ohno
                if (this.canDead(this.totalSegments) && this.tryFindSegment) this.kill();
            },
            // Any event you need segments has to verify if the term segment search
            timeOutSegment() {
                // remove time
                this.timeOut -= 1 * Time.delta;
                // stop
                if (this.timeOut < 0) this.tryFindSegment = true;
            },
            canDead(amount) {
                return amount < 1
            },
            createSegments() {
                // :b anti error
                if (unit.body != null && unit.end != null) {
                    // create childs
                    let total = unit.lengthSnake - this.totalSegments;
                    for (let i = 0; i < total; i++) {
                        // last unit
                        let lastSegment = this._segments[i - 1] ? this._segments[i - 1] : this.self;
                        // unit
                        let segment = i + 1 == total ? unit.end.create(this.team) : unit.body.create(this.team);
                        // caculated pos
                        Tmp.v1.trns(this.rotation, -(segment.hitSize + 10));
                        Tmp.v1.add(lastSegment.x, lastSegment.y);
                        // set unit
                        segment.setParent(this.self);
                        // set x,y
                        segment.set(Tmp.v1.x, Tmp.v1.y);
                        // rotation
                        segment.rotation = Math.atan2(lastSegment.y - segment.y, lastSegment.x - segment.x) * 180 / Math.PI;
                        ;
                        // set segment
                        segment.setSegment(lastSegment);
                        // multiplayer compatibility
                        Events.fire(new UnitCreateEvent(segment, null, this.self));
                        if (!Vars.net.client()) {
                            segment.add();
                        }
                        // register segment
                        this.addChild(segment);
                    }
                    // set on snake
                    this.setSneak = true;
                } else logLib("e", "body or end are null");
            },
            addChild(child) {
                if (child != null) {
                    // add child
                    this._segments.push(child);
                    // updateList
                    this.totalSegments = this._segments.length;
                } else logLib("w", "child is null - " + child)
            },
            write(write) {
                this.super$write(write);
                // write id
                write.i(this.id);
                // write snake its on
                write.bool(this.setSneak);
            },
            read(read) {
                this.super$read(read);
                // read id
                this.id = read.i();
                // read snake its on
                this.setSneak = read.bool();
            },
            classId: () => unit.classId
        }, constructor));
        registerClass(unit);
        return unit;
    }
};