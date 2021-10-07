package ru.sodajl.chordconsonanceanalyzer

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.ReplaySubject
import io.reactivex.rxjava3.subjects.Subject
import org.junit.Test
import org.reactivestreams.Subscription


class RxTests {

	@Test()
	fun subscriptionsTest() {
		val values: Subject<Int>
		values = ReplaySubject.create()
		val subscription: Disposable = values.subscribe(
			{value -> println(value)},
			{error -> println(error)},
			{println("Completed")}
		)
		values.onNext(1)
		values.onNext(2)
		values.onNext(3)
		values.onError(Exception("Omg!"))
		subscription.dispose()
		values.onComplete()
	}
}