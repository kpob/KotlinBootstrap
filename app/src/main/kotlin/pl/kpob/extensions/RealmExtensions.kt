package pl.kpob.extensions

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmQuery
import java.io.IOException

/**
 * Created by krzysztofpobiarzyn on 11.01.2017.
 */
fun realmInstance(): Realm = Realm.getDefaultInstance()

inline fun useRealm(action: Realm.() -> Unit) {
    realmInstance().use {
        it.action()
    }
}

inline fun realmTransaction(crossinline transaction: Realm.() -> Unit) {
    realmInstance().use {
        it.executeTransaction {
            it.transaction()
        }
    }
}

inline fun realmAsyncTransaction(crossinline transaction: Realm.() -> Unit) {
    realmInstance().use {
        it.executeTransactionAsync {
            it.transaction()
        }
    }
}

inline fun <reified T: RealmObject> Realm.where(): RealmQuery<T> = where(T::class.java)

open class RealmString(open var value: String = "") : RealmObject()
open class RealmLong(open var value: Long = 0L) : RealmObject()
open class RealmBoolean(open var value: Boolean = false) : RealmObject()

class RealmStringAdapter : TypeAdapter<RealmString>() {

    override fun read(`in`: JsonReader?): RealmString? {
        if (`in`?.hasNext() ?: false) {
            val nextStr = `in`?.nextString()
            return RealmString(nextStr!!)
        }

        return null
    }

    override fun write(out: JsonWriter?, value: RealmString?) {
        out?.value(value?.value)
    }
}

class RealmBoolenAdapter : TypeAdapter<RealmBoolean>() {

    override fun read(`in`: JsonReader?): RealmBoolean? {
        if (`in`?.hasNext() ?: false) {
            val nextStr = `in`?.nextBoolean()
            return RealmBoolean(nextStr!!)
        }

        return null
    }

    override fun write(out: JsonWriter?, value: RealmBoolean?) {
        out?.value(value?.value)
    }
}

class RealmStringListTypeAdapter: TypeAdapter<RealmList<RealmString>>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: RealmList<RealmString>) {
        // Ignore
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): RealmList<RealmString> {
        val list = RealmList<RealmString>()
        `in`.beginArray()
        while (`in`.hasNext()) {
            list.add(RealmString(`in`.nextString()))
        }
        `in`.endArray()
        return list
    }
}

class RealmBooleanListTypeAdapter: TypeAdapter<RealmList<RealmBoolean>>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: RealmList<RealmBoolean>) {
        // Ignore
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): RealmList<RealmBoolean> {
        val list = RealmList<RealmBoolean>()
        `in`.beginArray()
        while (`in`.hasNext()) {
            list.add(RealmBoolean(`in`.nextBoolean()))
        }
        `in`.endArray()
        return list
    }
}


fun List<Boolean>.toRealmList() : RealmList<RealmBoolean> {
    val list = map(::RealmBoolean)
    return RealmList<RealmBoolean>().apply { addAll(list) }
}

inline fun <reified T: RealmObject> T.nextId(realm: Realm) : Int {
    return realm.where<T>().count().toInt() + 1
}

fun <T : RealmObject> T.save(realm: Realm) {
    realm.executeTransaction {
        if(this.hasPrimaryKey(it)) it.copyToRealmOrUpdate(this) else it.copyToRealm(this)
    }
}

fun <T : Collection<RealmObject>> T.saveAll(realm: Realm) {
    realm.executeTransaction {
        it.insertOrUpdate(this)
    }
}

fun  Array<out RealmObject>.saveAll(realm: Realm) {
    realm.executeTransaction {
        forEach { if(it.hasPrimaryKey(realm)) realm.insertOrUpdate(it) else realm.insert(it) }
    }
}

fun <T : RealmObject> T.deleteAll(realm: Realm) {
    realm.executeTransaction { it.forEntity(this).findAll().deleteAllFromRealm() }
}

fun <T : RealmObject> T.delete(realm: Realm, myQuery: (RealmQuery<T>) -> Unit) {
    realm.executeTransaction {
        it.forEntity(this).withQuery(myQuery).findAll().deleteAllFromRealm()
    }
}

private fun <T : RealmObject> Realm.forEntity(instance : T) : RealmQuery<T>{
    return RealmQuery.createQuery(this, instance.javaClass)
}

private fun <T> T.withQuery(block: (T) -> Unit): T { block(this); return this }

private fun <T : RealmObject> T.hasPrimaryKey(realm : Realm) = realm.schema.get(this.javaClass.simpleName).hasPrimaryKey()







