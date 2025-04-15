package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * WeakRingBuffer — потокобезопасная кольцевая (циклическая) структура хранения фиксированной ёмкости,
 * использующая слабые ссылки на элементы, чтобы сборщик мусора мог очищать неиспользуемые объекты.
 *
 * @param <T> тип хранимых элементов
 */
public class WeakRingCircleBuffer<T> implements Iterable<T> {

    private final WeakReference<T>[] buffer;
    private final int capacity;
    private int start = 0;
    private int size = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Конструктор.
     *
     * @param capacity максимальный размер буфера
     */
    public WeakRingCircleBuffer(int capacity) {
        this.capacity = capacity;
        // Инициализируем массив слабых ссылок
        this.buffer = (WeakReference<T>[]) new WeakReference[capacity];
    }

    /**
     * Добавляет элемент в буфер. Если буфер переполнен, старейший элемент вытесняется.
     *
     * @param element элемент для добавления
     */
    public void add(T element) {
        lock.writeLock().lock();
        try {

            int index = (start + size) % capacity;


            buffer[index] = new WeakReference<>(element);

            if (size < capacity) {
                size++;
            } else {
                start = (start + 1) % capacity;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Возвращает список элементов из заданного диапазона (по логическому индексу).
     *
     * @param fromInclusive начало диапазона (включительно)
     * @param toExclusive   конец диапазона (не включительно)
     * @return список элементов в заданном диапазоне, возможно содержащий null
     * @throws IndexOutOfBoundsException если диапазон некорректен
     */
    public List<T> getRange(int fromInclusive, int toExclusive) {
        lock.readLock().lock(); // блокировка на чтение
        try {
            if (fromInclusive < 0 || toExclusive > size || fromInclusive > toExclusive) {
                throw new IndexOutOfBoundsException("Invalid range");
            }

            List<T> result = new ArrayList<>(toExclusive - fromInclusive);
            for (int i = fromInclusive; i < toExclusive; i++) {
                int index = (start + i) % capacity;
                WeakReference<T> ref = buffer[index];
                T obj = (ref != null) ? ref.get() : null;
                result.add(obj);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Возвращает текущее количество элементов в буфере
     *
     * @return логический размер буфера
     */
    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Возвращает итератор по текущим элементам буфера от самого старого до самого нового
     *
     * @return итератор по элементам
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                int realIndex = (start + index) % capacity;

                WeakReference<T> ref = buffer[realIndex];
                index++;

                return ref != null ? ref.get() : null;
            }
        };
    }
}
